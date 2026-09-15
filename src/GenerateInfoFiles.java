import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateInfoFiles {
    public static void main(String[] args) {
        createProductsFile(10);
        createSalesManInfoFile(10);

        /* Genera un archivo de ventas para cada uno de los 10 vendedores
         * creados arriba, usando la misma fórmula de número de documento
         * (1000000000L + i) que utiliza createSalesManInfoFile. */
        for (int i = 0; i < 10; i++) {
            long documentNumber = 1000000000L + i;
            createSalesMenFile(15, "vendedor", documentNumber);
        }
    }

    /**
     * Genera un archivo de productos.
     * @param productsCount Cantidad de productos que se desea generar.
     */
    public static void createProductsFile(int productsCount) {

        /* Listado de nombres para los productos. */
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

        /* Instance de la clase random para generar precios */
        Random random = new Random();

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter("products.txt"));

            for (int i = 0; i < productsCount; i++) {

                int productId = i + 1;

                String productName = productNames[i];

                int price = 2000 + random.nextInt(15500);

                writer.write(productId + ";" + productName + ";" + price);

                writer.newLine();
            }

            writer.close();

            System.out.println("Archivo products.txt creado correctamente.");

        } catch (IOException e) {
            System.out.println("Error al crear el archivo.");
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

        /* Instancia de Random para elegir nombres, apellidos y números de documento. */
        Random random = new Random();

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter("vendedores.txt"));

            for (int i = 0; i < salesmanCount; i++) {

                String firstName = firstNames[random.nextInt(firstNames.length)];
                String lastName = lastNames[random.nextInt(lastNames.length)];

                /* Número de documento único, generado a partir de un rango fijo más el índice. */
                long documentNumber = 1000000000L + i;

                writer.write(documentType + ";" + documentNumber + ";" + firstName + ";" + lastName);
                writer.newLine();
            }

            writer.close();

            System.out.println("Archivo vendedores.txt creado correctamente.");

        } catch (IOException e) {
            System.out.println("Error al crear el archivo de vendedores.");
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
     * @param name             texto usado para nombrar el archivo generado
     * @param id                número de documento del vendedor dueño del archivo
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) {

        /* Tipo de documento por defecto, igual que en createSalesManInfoFile. */
        final String documentType = "CC";

        /* Ids de producto válidos, leídos del catálogo ya generado. */
        List<Integer> availableProductIds = readProductIds();

        /* Instancia de Random para elegir productos y cantidades. */
        Random random = new Random();

        String fileName = "ventas_" + name + "_" + id + ".txt";

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            writer.write(documentType + ";" + id);
            writer.newLine();

            for (int i = 0; i < randomSalesCount; i++) {

                int productId = availableProductIds.get(random.nextInt(availableProductIds.size()));

                int quantitySold = 1 + random.nextInt(20);

                writer.write(productId + ";" + quantitySold + ";");
                writer.newLine();
            }

            writer.close();

            System.out.println("Archivo " + fileName + " creado correctamente.");

        } catch (IOException e) {
            System.out.println("Error al crear el archivo de ventas de " + name + ".");
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

        try {

            BufferedReader reader = new BufferedReader(new FileReader("products.txt"));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    productIds.add(Integer.parseInt(line.split(";")[0]));
                }
            }

            reader.close();

        } catch (IOException e) {

            /* products.txt todavía no existe: se usa un catálogo de respaldo
             * para que el método pueda seguir funcionando de forma aislada. */
            for (int i = 1; i <= 10; i++) {
                productIds.add(i);
            }
        }

        return productIds;
    }
}