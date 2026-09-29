import java.io.IOException;

/**
 * Clase TEMPORAL para validar, de punta a punta, que los reportes de
 * Giovanny (ReportGenerator) funcionan con datos reales: genera los
 * archivos de prueba, los procesa con SalesProcessor (parte de Marlon) y
 * finalmente escribe los dos CSV de Giovanny.
 * <p>
 * Es, básicamente, un ensayo de lo que hará el main() final que arma
 * Fernando. Se debe borrar de src/ una vez ese main() quede integrado,
 * para no dejar dos puntos de entrada compitiendo.
 */
public class PruebaIntegracionGiovanny {

    public static void main(String[] args) {

        /* 1. Genera products.txt, vendedores.txt y los ventas_*.txt
         *    (parte ya terminada, de Entrega 1). */
        GenerateInfoFiles.main(args);

        /* 2. Lee esos archivos y calcula dinero por vendedor y unidades
         *    por producto (parte de Marlon). */
        SalesProcessor processor = new SalesProcessor();

        try {
            processor.process(".");
        } catch (IOException e) {
            System.out.println("Error al procesar los archivos generados: " + e.getMessage());
            return;
        }

        /* 3. Ordena esa información y escribe los dos reportes en CSV
         *    (parte de Giovanny). */
        ReportGenerator.createSalesmenReport(processor);
        ReportGenerator.createProductsReport(processor);

        System.out.println("Revisa reporte_vendedores.csv y reporte_productos.csv en la raiz del proyecto.");
    }
}
