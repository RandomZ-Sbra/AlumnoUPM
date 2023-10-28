package cc.controlAlmacen;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.console.shadow.picocli.CommandLine.Tracer;

public class controlAlmacenTest {
    private static Semaphore mutex = new Semaphore(1);
    private static volatile int track = 0;
    private static Map<String, Integer> mapaProd;
    private static volatile ControlAlmacen cr;
    private static int testNum = 0;
    private static HashMap<Integer, String> trace;

    @BeforeAll
    public void init() {
        mapaProd = new HashMap<>();
        mapaProd.put("peras", 6);
        mapaProd.put("manzanas", 3);
        mapaProd.put("naranjas", 10);
        cr = new ControlAlmacenMonitor(mapaProd);
    }

    @BeforeEach
    public void parseTest() {
        testNum++;

    }

    private static class Cliente extends Thread implements BiPredicate<String, Integer> {
        private final String nombre;
        private int threadNum;
        private volatile String itemIndice;
        private final ControlAlmacen cr;
        private BiConsumer<String, Integer> toDo;
        private int cantidad;
        private boolean status;

        public Cliente(String nombre, ControlAlmacen cr) {
            this.nombre = nombre;
            this.cr = cr;
        }

        public void setTest(BiConsumer<String, Integer> toBuy) {
            this.toDo = toBuy;
        }

        @Override
        public void run() {
            try {
                super.run();
                mutex.acquire();
                track++;
                threadNum = track;
                System.out.println("[" + track + "]: " + nombre + " ha iniciado su test comprando " + cantidad
                        + " unidades de " + itemIndice);
                throw new NullPointerException();
                //toDo.accept(itemIndice, cantidad);
                //mutex.release();
            } catch (Exception e) {
                System.out.println(TestHandler.handleException(e));
                //assertTrue(false, TestHandler.handleException(e));
            }
        }

        @Override
        public boolean test(String itemId, Integer cantidad) {
            try {
                status = true;
                this.itemIndice = itemId;
                this.cantidad = cantidad;
                this.start();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public class Fabrica extends Thread implements BiPredicate<String, Integer> {
        private volatile String itemId;
        private int threadNum;
        private final ControlAlmacen cr;
        private BiConsumer<String, Integer> toDo;
        private int cantidad;
        private boolean status;

        public Fabrica(String itemId, ControlAlmacen cr) {
            this.itemId = itemId;
            this.cr = cr;
        }

        public void setTest(BiConsumer<String, Integer> reabastece) {
            this.toDo = reabastece;
        }

        @Override
        public void run() {
            try {
                super.run();
                mutex.acquire();
                track++;
                threadNum = track;
                System.out.println(
                        "[" + track + "]: La fabrica de " + itemId + " comienza a producir " + cantidad + " unidades.");
                toDo.accept(itemId, cantidad);
                mutex.release();
            } catch (Exception e) {
                System.out.println(TestHandler.handleException(e));
                assertTrue(false, TestHandler.handleException(e));
            }
        }

        @Override
        public boolean test(String itemId, Integer cantidad) {
            this.cantidad = cantidad;
            this.itemId = itemId;
            this.start();
            return true;
        }
    }

    @Test
    public void test_01() {
        try {
            Cliente c1 = new Cliente("c1", cr);
            Cliente c2 = new Cliente("c2", cr);
            Fabrica manzanas = new Fabrica("manzanas", cr);
            Fabrica peras = new Fabrica("peras", cr);
            c1.setTest((itm, cant) -> {
                System.out.println("[" + c1.threadNum + "]: " + c1.nombre + " compra " + cant + " unidades de " + itm);
                assertTrue(c1.cr.comprar(c1.nombre, itm, cant));
            });

        } catch (Exception e) {
            assertTrue(false, TestHandler.handleError(e));
        }
    }
    // End


    private static Set<String> catcher(String dir) {
        return Stream.of(new File(dir).listFiles())
          .map(File::getName)
          .collect(Collectors.toSet());
    }
    public static void main(String[] args) throws Exception {
        System.out.println(TestHandler.parser().toString().replace(";,", ";\n"));
        String a = "Test";
        System.out.println(a.contains("@Test"));
        Path path = Paths.get("./");
        System.out.println(catcher(path.toString()));

    }

    private static class TestHandler {
        private static boolean isTest = false;

        public static String handleError(Throwable err) {
            StackTraceElement[] stackTrace = err.getStackTrace();
            String locateErr = null;
            for (StackTraceElement element : stackTrace) {
                if (element.getClassName().equals(controlAlmacenTest.class.getName())) {
                    locateErr = "\nWhile executing the call to: " + element.getClassName() + "."
                            + element.getMethodName() + "()" + "\n\n";
                    // Aquí se puede editar el mensaje de error hasta dar un "full trace". Yo lo
                    // dejo en lo básico dando el número de línea donde el test falla y los valores
                    // esperados que da por defecto el mensaje de error de junit AssertionError.
                    break;
                }
            }
            return locateErr;
        }

        public static String handleException(Exception ex) {
            StackTraceElement[] stackTrace = ex.getStackTrace();
            String locateEx = null;
            for (StackTraceElement element : stackTrace) {
                locateEx += "\nAn exception rised while executing the call to: " + element.getClassName() + "."
                        + element.getMethodName() + "()" + "\n" + "An " + ex.getClass().getName() + " was thrown although it was not expected to." + "\n\n";
                // Aquí se puede editar el mensaje de error hasta dar un "full trace". Yo lo
                // dejo en lo básico dando el número de línea donde el test falla y los valores
                // esperados que da por defecto el mensaje de error de junit AssertionError.
                break;
            }
            return locateEx;
        }

        static int aux = 0;
        public static HashMap<Integer, List<String>> parser() {
            try {
                int testNum = 0;
                int keys = 0;
                boolean beginTest = false;
                List<String> buff = null;
                BufferedReader reader = new BufferedReader(
                        new FileReader("./src/cc/controlAlmacen/controlAlmacenTest.java"));
                ArrayList<String> lines = new ArrayList<>(reader.lines().toList());
                reader.close();
                lines.removeIf((line) -> {
                    if (!isTest)
                        isTest = line.contains("@Test");
                    else { //a
                        boolean continueTest = line.contains("}");
                        aux += line.contains("{")? 1 : 0;
                        aux -= line.contains("}")? 1 : 0;
                        if (continueTest && aux == 0)
                            isTest = false;
                        return false;
                    }
                    return true;
                });
                HashMap<Integer, List<String>> res = new HashMap<>();
                for (String string : lines) {
                    if (!beginTest)
                        beginTest = string.contains("public void test");
                    else { //b
                        if (keys == 0) {
                            keys += string.contains("{")? 1 : 0;
                            keys -= string.contains("}")? 1 : 0;
                            if (testNum != 0) {
                                res.put(testNum, buff);
                                beginTest = false;
                            }
                            testNum++;
                            buff = new ArrayList<>();
                        } else if (!string.isBlank()) {
                            keys += string.contains("{")? 1 : 0;
                            keys -= string.contains("}")? 1 : 0;
                            buff.add(string);
                        }
                    }
                }
                return res;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
