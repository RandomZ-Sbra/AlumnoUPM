import java.util.ArrayList;
import java.util.List;

public class CC_02_Carrera2 {
    private static List<Integer> listaCompartida = new ArrayList<Integer>();

    public static void main(String[] args) {
        Thread hiloProductorPar = new Thread(new ProductorPar());
        Thread hiloProductorImpar = new Thread(new ProductorImpar());

        hiloProductorPar.start();
        hiloProductorImpar.start();

        try {
            hiloProductorPar.join();
            hiloProductorImpar.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < listaCompartida.size(); i++) {
            System.out.println(listaCompartida.get(i));
        }
    }

    static class ProductorPar implements Runnable {
        public void run() {
            for (int i = 0; i < 10; i += 2) {
                listaCompartida.add(i);
            }
        }
    }

    static class ProductorImpar implements Runnable {
        public void run() {
            for (int i = 1; i < 10; i += 2) {
                listaCompartida.add(i);
            }
        }
    }
}
