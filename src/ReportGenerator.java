import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Clase encargada de los dos reportes que le corresponden a Giovanny en la
 * Entrega 2: el reporte de vendedores (punto 3 del enunciado) y el reporte
 * de productos (punto 4 del enunciado).
 * <p>
 * Esta clase no lee archivos ni hace cálculos: recibe un
 * {@link SalesProcessor} ya procesado (con {@link SalesProcessor#process}
 * ya ejecutado) y solo se encarga de ordenar esa información y escribirla
 * en el formato CSV que pide el enunciado.
 */
public class ReportGenerator {

    /** Nombre del archivo de reporte de vendedores. */
    private static final String SALESMEN_REPORT_FILE_NAME = "reporte_vendedores.csv";

    /** Nombre del archivo de reporte de productos. */
    private static final String PRODUCTS_REPORT_FILE_NAME = "reporte_productos.csv";

    /**
     * Genera el archivo reporte_vendedores.csv (punto 3 del enunciado):
     * un vendedor por línea, con su nombre completo y el dinero total que
     * recaudó, separados por punto y coma, ordenado de mayor a menor
     * recaudo.
     *
     * @param processor un SalesProcessor sobre el que ya se llamó
     *                  {@link SalesProcessor#process(String)}
     */
    public static void createSalesmenReport(SalesProcessor processor) {

        Map<String, Long> moneyBySalesman = processor.getMoneyBySalesman();

        /* Se pasan las entradas del mapa a una lista para poder ordenarlas;
         * un Map no garantiza ningún orden por sí solo. La clave todavía es
         * el documento del vendedor ("TipoDoc;NumeroDoc"), no su nombre. */
        List<Map.Entry<String, Long>> vendedoresOrdenados = new ArrayList<>(moneyBySalesman.entrySet());

        /* Se ordena de mayor a menor recaudo: por eso se compara
         * "vendedor2 contra vendedor1" y no al revés. */
        Collections.sort(vendedoresOrdenados, (vendedor1, vendedor2) ->
                Long.compare(vendedor2.getValue(), vendedor1.getValue()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESMEN_REPORT_FILE_NAME))) {

            for (Map.Entry<String, Long> vendedor : vendedoresOrdenados) {

                /* Aquí sí se traduce la clave "TipoDoc;NumeroDoc" al nombre
                 * completo del vendedor, usando el propio SalesProcessor. */
                String nombreCompleto = processor.getSalesmanName(vendedor.getKey());

                writer.write(nombreCompleto + ";" + vendedor.getValue());
                writer.newLine();
            }

            System.out.println("Archivo " + SALESMEN_REPORT_FILE_NAME + " creado correctamente.");

        } catch (IOException e) {
            System.out.println("Error al crear el reporte de vendedores: " + e.getMessage());
        }
    }

    /**
     * Genera el archivo reporte_productos.csv (punto 4 del enunciado):
     * un producto por línea, con su nombre y su precio, separados por
     * punto y coma, ordenado de mayor a menor cantidad vendida.
     *
     * @param processor un SalesProcessor sobre el que ya se llamó
     *                  {@link SalesProcessor#process(String)}
     */
    public static void createProductsReport(SalesProcessor processor) {

        Map<String, Long> unitsByProduct = processor.getUnitsByProduct();
        Map<String, String> productNames = processor.getProductNames();
        Map<String, Long> productPrices = processor.getProductPrices();

        /* Se ordena por cantidad vendida, así que se parte del mapa de
         * unidades (no del de nombres ni del de precios). La clave todavía
         * es el ID del producto, no su nombre. */
        List<Map.Entry<String, Long>> productosOrdenados = new ArrayList<>(unitsByProduct.entrySet());

        Collections.sort(productosOrdenados, (producto1, producto2) ->
                Long.compare(producto2.getValue(), producto1.getValue()));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_REPORT_FILE_NAME))) {

            for (Map.Entry<String, Long> producto : productosOrdenados) {

                String idProducto = producto.getKey();
                String nombreProducto = productNames.get(idProducto);
                Long precio = productPrices.get(idProducto);

                /* Si un id de producto vendido no aparece en products.txt
                 * (dato incoherente), se salta en lugar de escribir una
                 * línea con "null". La validación completa es de Marlon
                 * en la Entrega 3, pero esta guarda evita que el reporte
                 * se rompa mientras tanto. */
                if (nombreProducto != null && precio != null) {
                    writer.write(nombreProducto + ";" + precio);
                    writer.newLine();
                }
            }

            System.out.println("Archivo " + PRODUCTS_REPORT_FILE_NAME + " creado correctamente.");

        } catch (IOException e) {
            System.out.println("Error al crear el reporte de productos: " + e.getMessage());
        }
    }
}
