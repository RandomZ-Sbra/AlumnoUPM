public class CC_01_Threads implements Runnable {
    final int currentThread, maxThreads;
    final long sleep_ms;
    private String desc;

    /**
     * Constructor público que generará la thread 0 y el resto de threads indicadas
     * de forma recursiva.
     * 
     * @param maxThreads es el número máximo de threads que se deben generar,
     *                   contando con que la thread original es la 1.
     * @param sleep_ms   es el tiempo total que todas las threads pararán.
     */
    public CC_01_Threads(int maxThreads, long sleep_ms) {
        this.maxThreads = maxThreads - 1;
        this.currentThread = 0;
        this.sleep_ms = sleep_ms;
        this.desc = "Using thread " + currentThread + " with description: " + this.getClass().getCanonicalName() + " "
                + this.getClass().hashCode();
    }

    /**
     * Constructor privado para generar las sub-threads recursivas.
     * 
     * @param maxThreads es el número máximo de threads que se deben generar,
     *                   contando con que la thread original es la 1.
     * @param sleep_ms   es el tiempo total que todas las threads pararán.
     * @param current    es la thread actual, cada vez que se pasa de forma
     *                   recursiva será {@code this + 1}.
     */
    private CC_01_Threads(int maxThreads, long sleep_ms, int current) {
        this.maxThreads = maxThreads;
        this.currentThread = current;
        this.sleep_ms = sleep_ms;
        this.desc = "Using thread " + currentThread + " with description: " + this.getClass().getCanonicalName() + " "
                + this.getClass().hashCode();
    }

    @Override
    public void run() {
        if (currentThread < maxThreads) {
            Thread subThread = new Thread(new CC_01_Threads(maxThreads, sleep_ms, currentThread + 1));
            subThread.start();
            // Si se desea que se ejecuten de una en una se puede emplear subThread.join()
            // para que se ejecuten en escalera.
        }
        System.out.println(desc);
        System.out.println("Thread " + currentThread + " now sleeping for " + sleep_ms + "ms");
        try {
            Thread.sleep(sleep_ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("Unable to sleep thread " + currentThread + ". Stopping this thread now");
        }
        System.out.println(desc);
    }

    public static void main(String[] args) {
        // He preferido ser algo original y crear una thread que invoca a nuevas threads
        // cada una pasándole los datos "maxThreads", "currentThread" y "sleep_ms" a la
        // siguiente.
        // Cada thread llama a la siguiente y luego indica su estatus antes y después de
        // parar durante "sleep_ms" ms.
        int threads = 1 + (int) (Math.random() * (10 - 1));
        long sleepTime = 500 + (long) (Math.random() * (15000 - 500));
        Runnable r = new CC_01_Threads(threads, sleepTime);
        Thread t = new Thread(r);
        t.start();
    }

}
