import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class CC_02_Carrera {
  private Queue<Integer> colaCompartida;
  private volatile Semaphore semaforo;
  private static final int CAPACIDAD_MAXIMA = 5;

  public CC_02_Carrera() {
    colaCompartida = new LinkedList<Integer>();
    semaforo = new Semaphore(1); // Inicializa el semáforo con un permiso
  }

  public void iniciarCarrera() {
    Thread hiloProductor1 = new Thread(new ProductorPar());
    Thread hiloProductor2 = new Thread(new ProductorImpar());
    Thread hiloConsumidor = new Thread(new Consumidor());

    hiloProductor1.start();
    hiloProductor2.start();
    hiloConsumidor.start();
  }

  private class ProductorPar implements Runnable {
    public void run() {
      for (int i = 0; i < 10; i += 2) {
        try {
          semaforo.acquire(); // Adquiere un permiso del semáforo
          if (colaCompartida.size() < CAPACIDAD_MAXIMA) {
            colaCompartida.add(i);
            System.out.println("ProductorPar escribió " + i);
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          semaforo.release(); // Libera un permiso del semáforo
        }
      }
    }
  }

  private class ProductorImpar implements Runnable {
    public void run() {
      for (int i = 1; i < 10; i += 2) {
        try {
          semaforo.acquire(); // Adquiere un permiso del semáforo
          if (colaCompartida.size() < CAPACIDAD_MAXIMA) {
            colaCompartida.add(i);
            System.out.println("ProductorImpar escribió " + i);
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          semaforo.release(); // Libera un permiso del semáforo
        }
      }
    }
  }

  private class Consumidor implements Runnable {
    public void run() {
      for (int i = 0; i < 5; i++) { // Consumir 5 elementos de la cola
        try {
          semaforo.acquire(); // Adquiere un permiso del semáforo
          if (!colaCompartida.isEmpty()) {
            int valor = colaCompartida.remove();
            System.out.println("Consumidor leyó " + valor);
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          semaforo.release(); // Libera un permiso del semáforo
        }
      }
    }
  }

  public static void main(String[] args) {
    CC_02_Carrera carrera = new CC_02_Carrera();
    carrera.iniciarCarrera();
  }
}
