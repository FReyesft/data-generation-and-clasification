import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateInfoFiles {
    public static void main(String[] args) {
        createProductsFile(10);
        createSalesManInfoFile(10);
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
}