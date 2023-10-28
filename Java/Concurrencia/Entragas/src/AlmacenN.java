import es.upm.babel.cclib.Semaphore;

// TODO: importar la clase de los semáforos.
// 

// Almacén FIFO concurrente para N datos

class AlmacenN {
    private int capacidad = 0;
    private int[] almacenado = null;
    private int nDatos = 0;
    private int aExtraer = 0;
    private int aInsertar = 0;

    // TODO: declaración de los semáforos necesarios
    private static Semaphore[] almacenarSemaphore;
    private static Semaphore[] extraerSemaphore;
    private static Semaphore perms;

    public AlmacenN(int n) {
        capacidad = n;
        almacenado = new int[capacidad];
        nDatos = 0;
        aExtraer = 0;
        aInsertar = 0;

        // TODO: inicialización de los semáforos
        almacenarSemaphore = new Semaphore[capacidad];
        extraerSemaphore = new Semaphore[capacidad];
        perms = new Semaphore(1);

        for (int i = 0; i < capacidad; i++) {
            almacenarSemaphore[i] = new Semaphore(1); // Todos los huecos inicialmente vacíos.
            extraerSemaphore[i] = new Semaphore();  // Ningún hueco tiene nada para extraer...
        }
    }

    public void almacenar(int producto) {
        // TODO: protocolo de acceso a la sección crítica y código de
        // sincronización para poder almacenar.
        perms.await();
        almacenarSemaphore[aInsertar].await();
        if (producto < 0) {
            almacenarSemaphore[aInsertar].signal();
            perms.signal();
            return;
        }
        int current = aInsertar;
        
        System.out.println("Almacen[" + aInsertar + "]: <- " + producto);

        // Sección crítica //////////////
        almacenado[aInsertar] = producto;
        nDatos++;
        aInsertar++;
        aInsertar %= capacidad;
        // //////////////////////////////

        // TODO: protocolo de salida de la sección crítica y código de
        // sincronización para poder extraer.
        extraerSemaphore[current].signal();
        perms.signal();
        //
    }

    public int extraer() {
        int result;

        // TODO: protocolo de acceso a la sección crítica y código de
        // sincronización para poder extraer.
        perms.await();
        extraerSemaphore[aExtraer].await();
        int current = aExtraer;

        System.out.println("Almacen[" + aExtraer + "]: -> " + almacenado[aExtraer]);

        // Sección crítica ///////////
        result = almacenado[aExtraer];
        nDatos--;
        aExtraer++;
        aExtraer %= capacidad;
        //////////////////////////////

        // TODO: protocolo de salida de la sección crítica y código de
        // sincronización para poder almacenar.
        almacenarSemaphore[current].signal(); // Hueco libre
        perms.signal(); // Operación finalizada

        return result;
    }
}