import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.MultiAlmacen;

import java.util.concurrent.ArrayBlockingQueue;

// importar la librería de monitores
import es.upm.babel.cclib.Monitor;

class MultiAlmacenMon implements MultiAlmacen {
    private int capacidad = 0;
    private Producto almacenado[] = null;
    private int aExtraer = 0;
    private int aInsertar = 0;
    private int nDatos = 0;

    // TODO: declaración de atributos extras necesarios para exclusión mutua y
    // sincronización por condición.
    private static volatile Monitor mutex;
    private static volatile Monitor.Cond noData;
    private static volatile ArrayBlockingQueue<Producto> productos;

    // Para evitar la construcción de almacenes sin inicializar la
    // capacidad

    public MultiAlmacenMon(int n) {
        almacenado = new Producto[n];
        aExtraer = 0;
        aInsertar = 0;
        capacidad = n;
        nDatos = 0;

        // TODO: inicialización de otros atributos
        mutex = new Monitor();
        noData = mutex.newCond();
        productos = new ArrayBlockingQueue<>(n);
    }

    private int nDatos() {
        return nDatos;
    }

    private int nHuecos() {
        return capacidad - nDatos;
    }

    public void almacenar(Producto[] productos) {

        // TODO: implementación de código de bloqueo para
        // exclusión mutua y sincronización condicional
        mutex.enter();
        int err = 0;
        // Sección crítica
        for (int i = 0; i < productos.length; i++) {
            try {
                MultiAlmacenMon.productos.put(productos[i]); // Usaré una arrayBlockingQueue para evitar que se acceda
                                                             // si está llena.
                almacenado[aInsertar] = productos[i];
                nDatos++;
                aInsertar++;
                aInsertar %= capacidad;
            } catch (InterruptedException e) {
                i--;
                err++;
                e.printStackTrace();
                if (err > 5) {
                    i = productos.length;
                    System.err.println("ERR: Could not insert products after 5 retries, abandoning without inserting.");
                }
            }
        }

        // TODO: implementación de código de desbloqueo para
        // sincronización condicional y liberación de la exclusión mutua
        noData.signal();
        mutex.leave();
    }

    public Producto[] extraer(int n) {
        Producto[] result = new Producto[n];

        // TODO: implementación de código de bloqueo para exclusión
        // mutua y sincronización condicional
        mutex.enter();
        while (n > nDatos)
            noData.await();
        // Sección crítica
        for (int i = 0; i < result.length; i++) {
            result[i] = almacenado[aExtraer];
            almacenado[aExtraer] = null;
            nDatos--;
            aExtraer++;
            aExtraer %= capacidad;
        }

        // TODO: implementación de código de desbloqueo para
        // sincronización condicional y liberación de la exclusión mutua
        mutex.leave();
        return result;
    }
}