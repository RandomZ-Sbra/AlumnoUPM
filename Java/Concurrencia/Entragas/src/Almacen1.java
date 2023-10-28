
// TODO: importar la clase de los semáforos.
import es.upm.babel.cclib.Semaphore;

// Almacen concurrente para un dato
class Almacen1 {
    // Producto a almacenar
    private int almacenado;

    // TODO: declaración e inicialización de los semáforos
    // necesarios
    private static Semaphore almacenarSemaphore;
    private static Semaphore extraerSemaphore;
    private final int MAX_NUM = 199;

    public Almacen1() {
        Semaphore.setMeanSleepAfterAwait(50);
        almacenarSemaphore = new Semaphore(1);
        extraerSemaphore = new Semaphore();
        almacenado = -1;
    }

    public void almacenar(int producto) {
        // TODO: protocolo de acceso a la sección crítica y código de
        // sincronización para poder almacenar.
        //
        almacenarSemaphore.await();
        if (producto < 0) {
            almacenarSemaphore.signal();
            return;
        }

        System.out.println("Almacenando " + producto);

        // Sección crítica
        almacenado = producto;

        // TODO: protocolo de salida de la sección crítica y código de
        // sincronización para poder extraer.
        extraerSemaphore.signal();
    }

    public int extraer() {
        int result;

        // TODO: protocolo de acceso a la sección crítica y código de
        // sincronización para poder extraer.
        extraerSemaphore.await();
        System.out.println("Extrayendo " + almacenado);

        // Sección crítica
        result = almacenado;

        // TODO: protocolo de salida de la sección crítica y código de
        // sincronización para poder almacenar.
        if (result >= MAX_NUM)
            System.exit(0);
        almacenarSemaphore.signal();
        return result;
    }
}
