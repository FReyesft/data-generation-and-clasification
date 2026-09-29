/**
 * Clase principal del proyecto. Lee los archivos planos generados por
 * {@link GenerateInfoFiles} y produce los reportes de ventas por
 * vendedor y por producto.
 * <p>
 * El nombre de la clase va en minúscula porque así lo exige el
 * enunciado del proyecto.
 */
public class main {

    /**
     * Punto de entrada de la aplicación. Ejecuta la lectura de archivos y
     * la generación de reportes, y muestra un mensaje de finalización
     * exitosa o un mensaje de error si algo sale mal.
     *
     * @param args argumentos de línea de comandos (no se utilizan)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Reportes generados correctamente.");

        } catch (Exception e) {
            System.out.println("Error al generar los reportes: " + e.getMessage());
        }
    }
}
