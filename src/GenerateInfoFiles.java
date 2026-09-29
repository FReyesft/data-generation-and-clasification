import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Genera los archivos planos de prueba del proyecto: el catálogo de
 * productos (products.txt), la información de los vendedores
 * (vendedores.txt) y un archivo de ventas por cada vendedor
 * (ventas_&lt;nombre&gt;_&lt;id&gt;.txt).
 * <p>
 * Todos los datos se generan de forma pseudoaleatoria y no se solicita
 * ninguna información al usuario por consola.
 */
public class GenerateInfoFiles {

    /** Nombre del archivo plano con el catálogo de productos. */
    private static final String PRODUCTS_FILE_NAME = "products.txt";

    /** Nombre del archivo plano con la información de los vendedores. */
    private static final String SALESMEN_FILE_NAME = "vendedores.txt";

    /**
     * Punto de entrada del generador. Crea el archivo de productos, el
     * archivo de vendedores y, a partir de este último, un archivo de
     * ventas por cada vendedor usando su nombre y documento reales.
     *
     * @param args argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        createProductsFile(10);
        createSalesManInfoFile(10);

        /* Genera un archivo de ventas para cada vendedor registrado en
         * vendedores.txt, usando su nombre y número de documento reales. */
        try (BufferedReader reader = new BufferedReader(new FileReader(SALESMEN_FILE_NAME))) {

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                /* Formato: TipoDocumento;NúmeroDocumento;Nombres;Apellidos */
                String[] fields = line.split(";");
                long documentNumber = Long.parseLong(fields[1].trim());
                String salesmanName = fields[2].trim() + "_" + fields[3].trim();

                createSalesMenFile(15, salesmanName, documentNumber);
            }

            System.out.println("Generación de archivos finalizada correctamente.");

        } catch (IOException | RuntimeException e) {
            System.out.println("Error al leer el archivo " + SALESMEN_FILE_NAME
                    + " para generar las ventas: " + e.getMessage());
        }
    }

    /**
     * Genera un archivo plano con un número determinado de productos,
     * siguiendo el formato:
     * <pre>
     * IDProducto;NombreProducto;PrecioPorUnidadProducto
     * </pre>
     * Si se piden más productos que nombres base disponibles, los nombres
     * se reutilizan agregando un número de variante (por ejemplo,
     * "Arroz 2"). Los ids son consecutivos desde 1, por lo que nunca se
     * repiten.
     *
     * @param productsCount cantidad de productos que se desea generar
     */
    public static void createProductsFile(int productsCount) {

        /* Listado de nombres base para los productos. */
        String[] productNames = {
                "Arroz",
                "Leche",
                "Huevos",
                "Pan",
                "Aceite",
                "Azúcar",
                "Cafe",
                "Pasta",
                "Lentejas",
                "Panela"
        };

        /* Instancia de la clase Random para generar precios. */
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE_NAME))) {

            for (int i = 0; i < productsCount; i++) {

                int productId = i + 1;

                /* Nombre base más un número de variante cuando se agotan los nombres. */
                int variant = i / productNames.length + 1;
                String productName = productNames[i % productNames.length];
                if (variant > 1) {
                    productName = productName + " " + variant;
                }

                int price = 2000 + random.nextInt(15500);

                writer.write(productId + ";" + productName + ";" + price);
                writer.newLine();
            }

            System.out.println("Archivo " + PRODUCTS_FILE_NAME + " creado correctamente.");

        } catch (IOException e) {
            System.out.println("Error al crear el archivo " + PRODUCTS_FILE_NAME + ": " + e.getMessage());
        }
    }

    /**
     * Genera un archivo plano con la información pseudoaleatoria de un número
     * determinado de vendedores, siguiendo el formato:
     * <pre>
     * TipoDocumento;NúmeroDocumento;NombresVendedor;ApellidosVendedor
     * </pre>
     *
     * @param salesmanCount cantidad de vendedores a generar
     */
    public static void createSalesManInfoFile(int salesmanCount) {

        /* Listas de nombres y apellidos reales para generar información coherente. */
        String[] firstNames = {
                "Juan", "María", "Carlos", "Laura", "Andrés",
                "Camila", "Diego", "Valentina", "Santiago", "Daniela"
        };

        String[] lastNames = {
                "García", "Rodríguez", "Martínez", "López", "Gómez",
                "Hernández", "Pérez", "Sánchez", "Ramírez", "Torres"
        };

        /* Tipo de documento por defecto. */
        final String documentType = "CC";

        /* Instancia de Random para elegir nombres y apellidos. */
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESMEN_FILE_NAME))) {

            for (int i = 0; i < salesmanCount; i++) {

                String firstName = firstNames[random.nextInt(firstNames.length)];
                String lastName = lastNames[random.nextInt(lastNames.length)];

                /* Número de documento único, generado a partir de un rango fijo más el índice. */
                long documentNumber = 1000000000L + i;

                writer.write(documentType + ";" + documentNumber + ";" + firstName + ";" + lastName);
                writer.newLine();
            }

            System.out.println("Archivo " + SALESMEN_FILE_NAME + " creado correctamente.");

        } catch (IOException e) {
            System.out.println("Error al crear el archivo " + SALESMEN_FILE_NAME + ": " + e.getMessage());
        }
    }

    /**
     * Genera un archivo plano de ventas pseudoaleatorio para un vendedor,
     * siguiendo el formato:
     * <pre>
     * TipoDocumentoVendedor;NúmeroDocumentoVendedor
     * IDProducto1;CantidadProducto1Vendido;
     * IDProducto2;CantidadProducto2Vendido;
     * </pre>
     * Las líneas de venta hacen referencia únicamente a productos que ya
     * existan en el archivo products.txt (generado por
     * {@link #createProductsFile(int)}), para que la información quede
     * siempre coherente con el catálogo real. Si ese archivo todavía no
     * existe, se usa un catálogo de respaldo con los ids 1 a 10.
     *
     * @param randomSalesCount cantidad de líneas de venta a generar
     * @param name             nombre del vendedor, usado para nombrar el archivo generado
     * @param id               número de documento del vendedor dueño del archivo
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) {

        /* Tipo de documento por defecto, igual que en createSalesManInfoFile. */
        final String documentType = "CC";

        /* Ids de producto válidos, leídos del catálogo ya generado. */
        List<Integer> availableProductIds = readProductIds();

        /* Instancia de Random para elegir productos y cantidades. */
        Random random = new Random();

        String fileName = "ventas_" + name + "_" + id + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            writer.write(documentType + ";" + id);
            writer.newLine();

            for (int i = 0; i < randomSalesCount; i++) {

                int productId = availableProductIds.get(random.nextInt(availableProductIds.size()));

                int quantitySold = 1 + random.nextInt(20);

                writer.write(productId + ";" + quantitySold + ";");
                writer.newLine();
            }

            System.out.println("Archivo " + fileName + " creado correctamente.");

        } catch (IOException e) {
            System.out.println("Error al crear el archivo " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Lee los ids de producto disponibles en products.txt para poder
     * generar ventas coherentes con el catálogo real.
     * Si el archivo aún no existe, retorna un catálogo de respaldo con
     * los ids 1 a 10, para que createSalesMenFile nunca falle.
     *
     * @return la lista de ids de producto disponibles
     */
    private static List<Integer> readProductIds() {

        List<Integer> productIds = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE_NAME))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    productIds.add(Integer.parseInt(line.split(";")[0].trim()));
                }
            }

        } catch (IOException e) {

            /* products.txt todavía no existe: se usa un catálogo de respaldo
             * para que el método pueda seguir funcionando de forma aislada. */
            productIds.clear();
            for (int i = 1; i <= 10; i++) {
                productIds.add(i);
            }
        }

        return productIds;
    }
}
