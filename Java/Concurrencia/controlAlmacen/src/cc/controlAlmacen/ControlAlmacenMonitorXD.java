package cc.controlAlmacen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.upm.babel.cclib.Monitor;

public class ControlAlmacenMonitorXD implements ControlAlmacen {

  // Resource state: mapa de productos y monitores
  private ConcurrentHashMap<String, Producto> mapaProductos;

  private static Monitor mon;
  private static Monitor.Cond outMutex;
  private static boolean haciendoPeticion;

  private static Set<String> catcher(String dir) {
    return Stream.of(new File(dir).listFiles())
        .map(File::getName)
        .collect(Collectors.toSet());
  }

  private static boolean isTest = false;
  static int aux = 0;

  private static List<String> parser() {
    try {
      int testNum = 0;
      int keys = 0;
      boolean beginTest = false;
      List<String> buff = null;
      BufferedReader reader = new BufferedReader(
          new FileReader("./src/Tests.java"));
      ArrayList<String> lines = new ArrayList<>();
      String s;
      while ((s = reader.readLine()) != null) {
        lines.add(s);
      }
      reader.close();
      // lines.removeIf((line) -> {
      // if (!isTest)
      // isTest = line.contains("@Test");
      // else { //a
      // boolean continueTest = line.contains("}");
      // aux += line.contains("{")? 1 : 0;
      // aux -= line.contains("}")? 1 : 0;
      // if (continueTest && aux == 0)
      // isTest = false;
      // return false;
      // }
      // return true;
      // });
      HashMap<Integer, List<String>> res = new HashMap<>();
      // for (String string : lines) {
         // if (!beginTest)
         // beginTest = string.contains("public void test");
         // else { //b
         // if (keys == 0) {
         // keys += string.contains("{")? 1 : 0;
         // keys -= string.contains("}")? 1 : 0;
         // if (testNum != 0) {
         // res.put(testNum, buff);
         // beginTest = false;
         // }
         // testNum++;
         // buff = new ArrayList<>();
         // } else if (!string.isBlank()) {
         // keys += string.contains("{")? 1 : 0;
         // keys -= string.contains("}")? 1 : 0;
         // buff.add(string);
         // }
         // }
      // }
      return lines;
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    System.out.println("Not Good");
    return null;
  }

  // Clases auxiliares
  private static class Producto {
    private volatile String id;
    private volatile boolean inUse, reabastecer;
    private volatile Monitor.Cond mutexCond, reabastecerCond, entregarCond;
    private volatile ConcurrentLinkedQueue<Peticion> colaPeticiones;
    private volatile int disponibles, enCamino, comprados;
    private final int minDisponibles;

    /**
     * 
     * @param id
     * @param min
     */
    private Producto(String id, int min) {
      this.id = id;
      this.disponibles = min;
      this.enCamino = 0;
      this.comprados = 0;
      minDisponibles = min;
      colaPeticiones = new ConcurrentLinkedQueue<>();
      mutexCond = mon.newCond();
      reabastecerCond = mon.newCond();
      entregarCond = mon.newCond();
      inUse = false;
      reabastecer = true;
    }

    /**
     * 
     * @param map
     * @return
     */
    public static ConcurrentHashMap<String, Producto> fromMap(Map<String, Integer> map) {
      ConcurrentHashMap<String, Producto> res = new ConcurrentHashMap<>();
      map.forEach((str, min) -> {
        res.put(str, new Producto(str, min));
      });
      return res;
    }

    /**
     * 
     * @param request
     * @return
     */
    public boolean hacerPedido(Peticion request) {
      mon.enter();
      if (inUse)
        mutexCond.await();
      inUse = true;
      if (!this.disponible(request.getCantidad())) {
        System.out.println("No se pudo hacer el pedido. Por favor intentelo de nuevo más tarde...");
        inUse = false;
        mutexCond.signal();
        mon.leave();
        return false;
      } else {
        this.comprados += request.getCantidad();
        if (reabastecer = disponible(minDisponibles - 1))
          reabastecerCond.signal();
        inUse = false;
        mutexCond.signal();
        mon.leave();
        return true;
      }
    }

    /**
     * 
     * @param request
     */
    public void solicitarReabastecer(Peticion request) {
      mon.enter();
      if (!reabastecer)
        reabastecerCond.await();
      reabastecer = false;
      if (inUse)
        mutexCond.await();
      inUse = true;
      this.enCamino += request.getCantidad();
      inUse = false;
      //FIXME: ...
      mutexCond.signal();
      mon.leave();
    }

    /**
     * 
     * @param request
     */
    public void reabastecerProd(Peticion request) {
      mon.enter();
      if (inUse)
        mutexCond.await();
      inUse = true;
      this.enCamino -= request.getCantidad();
      this.disponibles += request.getCantidad();
      if (!colaPeticiones.isEmpty() && colaPeticiones.peek().getCantidad() <= disponibles) // CHEK2
        entregarCond.signal();
      inUse = false;
      mutexCond.signal();
      mon.leave();
    }

    /**
     * 
     * @param request
     */
    public void solicitardevolucion(Peticion request) {
      mon.enter();
      if (inUse)
        mutexCond.await();
      inUse = true;
      this.disponibles += request.getCantidad();
      if (!colaPeticiones.isEmpty() && colaPeticiones.peek().getCantidad() <= disponibles) // CHEK2
        entregarCond.signal();
      inUse = false;
      mutexCond.signal();
      mon.leave();
    }

    /**
     * 
     * @param request
     */
    public void hacerEntrega(Peticion request) {
      mon.enter();
      colaPeticiones.add(request);
      if (colaPeticiones.peek().getCantidad() >= disponibles) {
        entregarCond.await();
      }

      if (inUse)
        mutexCond.await();
      inUse = false;

      Peticion p = colaPeticiones.isEmpty() ? request : colaPeticiones.peek();
      comprados -= p.getCantidad();
      disponibles -= p.getCantidad();
      if (!disponible(minDisponibles - 1)) {
        reabastecerCond.signal();
      }
      colaPeticiones.poll();
      if (!colaPeticiones.isEmpty() && colaPeticiones.peek().getCantidad() < disponibles)
        entregarCond.signal();
      inUse = false;
      mutexCond.signal();
      mon.leave();
    }

    /**
     * 
     * @param cantidad
     * @return
     */
    public boolean disponible(int cantidad) {
      if (cantidad <= 0)
        return disponibles + enCamino - comprados > 0;
      return (disponibles + enCamino - comprados) >= cantidad;
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }
  }

  private static class Peticion {
    private String idCliente;
    private int cantidad;

    public Peticion(String idCliente, int cantidad) {
      if (idCliente == null || cantidad <= 0)
        throw new IllegalArgumentException(
            "ERR: Fallo al realizar el pedido. Revise si la cantidad solicitada es negativa");
      this.idCliente = idCliente;
      this.cantidad = cantidad;
    }

    public String getCliente() {
      return idCliente;
    }

    public int getCantidad() {
      return cantidad;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Peticion) {
        return Peticion.class.cast(obj).getCliente().equals(this.idCliente);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return idCliente.hashCode();
    }
  }

  // Metodos que nos piden en la entrega

  public ControlAlmacenMonitorXD(Map<String, Integer> tipoProductos) {
    mon = new Monitor();
    outMutex = mon.newCond();
    haciendoPeticion = false;
    mapaProductos = Producto.fromMap(tipoProductos);
    List<String> a = parser();
    for (String string : a) {
      System.err.println(string);
    }
  }

  public boolean comprar(String clientId, String itemId, int cantidad) {
    Peticion p = new Peticion(clientId, cantidad);
    if (!mapaProductos.containsKey(itemId))
      throw new IllegalArgumentException(
          "ERR: El producto introducido no es válido. Ningún producto del almacén concuerda con el id.");
    return mapaProductos.get(itemId).hacerPedido(p);
  }

  public void entregar(String clientId, String itemId, int cantidad) {
    mon.enter();
    if (haciendoPeticion)
      outMutex.await();
    haciendoPeticion = true;
    Peticion p = new Peticion(itemId, cantidad);
    if (!mapaProductos.containsKey(itemId)) {
      throw new IllegalArgumentException(
          "ERR: El producto introducido no es válido. Ningún producto del almacén concuerda con el id.");
    }
    haciendoPeticion = false;
    outMutex.signal();
    mon.leave();
    mapaProductos.get(itemId).hacerEntrega(p);
  }

  public void devolver(String clientId, String itemId, int cantidad) {
    Peticion p = new Peticion(itemId, cantidad);
    if (!mapaProductos.containsKey(itemId))
      throw new IllegalArgumentException(
          "ERR: El producto introducido no es válido. Ningún producto del almacén concuerda con el id.");
    mapaProductos.get(itemId).solicitardevolucion(p);
  }

  public void ofrecerReabastecer(String itemId, int cantidad) {
    Peticion p = new Peticion(itemId, cantidad);
    if (!mapaProductos.containsKey(itemId))
      throw new IllegalArgumentException(
          "ERR: El producto introducido no es válido. Ningún producto del almacén concuerda con el id.");
    mapaProductos.get(itemId).solicitarReabastecer(p);
  }

  public void reabastecer(String itemId, int cantidad) {
    Peticion p = new Peticion(itemId, cantidad);
    if (!mapaProductos.containsKey(itemId))
      throw new IllegalArgumentException(
          "ERR: El producto introducido no es válido. Ningún producto del almacén concuerda con el id.");
    mapaProductos.get(itemId).reabastecerProd(p);
  }
}