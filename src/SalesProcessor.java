import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lee los archivos de productos, vendedores y ventas generados por
 * {@link GenerateInfoFiles} y calcula el dinero recaudado por vendedor
 * y las unidades vendidas por producto.
 * <p>
 * Los vendedores se identifican por su documento ("TipoDoc;NumeroDoc"),
 * no por su nombre, porque dos vendedores pueden tener el mismo nombre.
 * Si un vendedor tiene varios archivos de ventas, sus totales se suman.
 */
public class SalesProcessor {

    /** Archivo con los productos: {@code ID;Nombre;Precio}. */
    public static final String PRODUCTS_FILE_NAME = "products.txt";

    /** Archivo con los vendedores: {@code TipoDoc;NumeroDoc;Nombres;Apellidos}. */
    public static final String SALESMEN_FILE_NAME = "vendedores.txt";

    /** Prefijo de los archivos de ventas de cada vendedor. */
    public static final String SALES_FILE_PREFIX = "ventas_";

    /** Separador de campos de todos los archivos. */
    private static final String SEPARATOR = ";";

    /** Nombre del producto por ID. */
    private final Map<String, String> productNames = new HashMap<>();

    /** Precio por unidad de cada producto, por ID. */
    private final Map<String, Long> productPrices = new HashMap<>();

    /** Nombre completo del vendedor, por clave "TipoDoc;NumeroDoc". */
    private final Map<String, String> salesmenNames = new LinkedHashMap<>();

    /** Dinero recaudado por vendedor, por clave "TipoDoc;NumeroDoc". */
    private final Map<String, Long> moneyBySalesman = new LinkedHashMap<>();

    /** Unidades vendidas por ID de producto. */
    private final Map<String, Long> unitsByProduct = new LinkedHashMap<>();

    /**
     * Carga productos y vendedores, procesa todos los archivos de ventas
     * de la carpeta indicada y calcula los totales. Puede llamarse varias
     * veces: cada llamada reinicia los resultados anteriores.
     *
     * @param folderPath ruta de la carpeta donde están los archivos
     * @throws IOException si algún archivo no puede leerse o tiene una
     *                     cabecera inválida
     */
    public void process(String folderPath) throws IOException {
        clearResults();

        File folder = new File(folderPath);
        loadProducts(new File(folder, PRODUCTS_FILE_NAME));
        loadSalesmen(new File(folder, SALESMEN_FILE_NAME));

        File[] salesFiles = folder.listFiles((dir, name) ->
                name.startsWith(SALES_FILE_PREFIX) && name.endsWith(".txt"));
        if (salesFiles == null) {
            throw new IOException("No se pudo leer la carpeta: " + folderPath);
        }
        for (File salesFile : salesFiles) {
            processSalesFile(salesFile);
        }
    }

    /** Reinicia todos los datos cargados y calculados. */
    private void clearResults() {
        productNames.clear();
        productPrices.clear();
        salesmenNames.clear();
        moneyBySalesman.clear();
        unitsByProduct.clear();
    }

    /**
     * Abre un archivo de texto leyéndolo en UTF-8.
     *
     * @param file archivo a abrir
     * @return lector listo para usar
     * @throws IOException si el archivo no existe o no puede abrirse
     */
    private BufferedReader openReader(File file) throws IOException {
        return new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    /**
     * Lee el archivo de productos y guarda nombre y precio por ID.
     *
     * @param file archivo products.txt
     * @throws IOException si el archivo no puede leerse
     */
    private void loadProducts(File file) throws IOException {
        try (BufferedReader reader = openReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length >= 3) {
                    String id = parts[0].trim();
                    productNames.put(id, parts[1].trim());
                    productPrices.put(id, Long.parseLong(parts[2].trim()));
                }
            }
        }
    }

    /**
     * Lee el archivo de vendedores. Todos quedan con 0 de dinero para que
     * aparezcan en el reporte aunque no tengan archivo de ventas.
     *
     * @param file archivo vendedores.txt
     * @throws IOException si el archivo no puede leerse
     */
    private void loadSalesmen(File file) throws IOException {
        try (BufferedReader reader = openReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);
                if (parts.length >= 4) {
                    String key = parts[0].trim() + SEPARATOR + parts[1].trim();
                    salesmenNames.put(key, parts[2].trim() + " " + parts[3].trim());
                    moneyBySalesman.put(key, 0L);
                }
            }
        }
    }

    /**
     * Procesa un archivo de ventas: suma el dinero al vendedor de la
     * cabecera y las unidades a cada producto vendido.
     *
     * @param file archivo ventas_*.txt
     * @throws IOException si el archivo no puede leerse o su cabecera es inválida
     */
    private void processSalesFile(File file) throws IOException {
        try (BufferedReader reader = openReader(file)) {
            String header = reader.readLine();
            if (header == null) {
                return;
            }
            String[] headerParts = header.split(SEPARATOR);
            if (headerParts.length < 2) {
                throw new IOException("Cabecera inválida en el archivo " + file.getName());
            }
            String key = headerParts[0].trim() + SEPARATOR + headerParts[1].trim();

            long total = moneyBySalesman.getOrDefault(key, 0L);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(SEPARATOR);   // ignora el ";" final
                if (parts.length < 2 || !productPrices.containsKey(parts[0].trim())) {
                    continue;   // la validación completa queda para la Entrega 3
                }
                String productId = parts[0].trim();
                long quantity = Long.parseLong(parts[1].trim());
                total += quantity * productPrices.get(productId);
                unitsByProduct.merge(productId, quantity, Long::sum);
            }
            moneyBySalesman.put(key, total);
        }
    }

    /**
     * @return dinero recaudado por vendedor, con clave "TipoDoc;NumeroDoc"
     *         (para el reporte del punto 3)
     */
    public Map<String, Long> getMoneyBySalesman() {
        return moneyBySalesman;
    }

    /**
     * Devuelve el nombre completo de un vendedor.
     *
     * @param key clave "TipoDoc;NumeroDoc" del vendedor
     * @return "Nombres Apellidos", o la misma clave si no está en vendedores.txt
     */
    public String getSalesmanName(String key) {
        return salesmenNames.getOrDefault(key, key);
    }

    /** @return unidades vendidas por ID de producto (para el reporte del punto 4) */
    public Map<String, Long> getUnitsByProduct() {
        return unitsByProduct;
    }

    /** @return nombre del producto por ID */
    public Map<String, String> getProductNames() {
        return productNames;
    }

    /** @return precio por unidad del producto, por ID */
    public Map<String, Long> getProductPrices() {
        return productPrices;
    }
}
