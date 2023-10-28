package cc.controlAlmacen;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import es.upm.babel.cclib.Monitor;

public class ControlAlmacenMonitor implements ControlAlmacen {

  // Resource state: mapa de productos y monitores
  private ConcurrentHashMap<String, Producto> mapaProductos;

  // Monitores, condiciones y variables auxiliares de las clase general:
  // ControlAlmacenMonitor
  private final Monitor mon;// CBDO
  // private final Monitor.Cond outMutex;//CBDO
  // private boolean haciendoPeticion;

  /**
   * 
   * La clase contiene los metodos principales que usan las funciones pedidas en
   * en enunciado.
   * En general vamos a trabajar con peticiones que contienen productos que son
   * los que están en mapa.
   * 
   * Cada producto en map tiene sus:
   * 
   * @param id              string id.
   * @param reabastecerCond condicion del monitor para permitir o bloquear
   *                        reabastecer.
   * @param entregarCond    condicion del monitor para permitir o bloquear una
   *                        entrega.
   * @param disponibles     numero de unidades diponibles.
   * @param enCamino        numero de unidades en camino.
   * @param comprados       numero de unidades compradas que aun no fueron
   *                        entregadas.
   * @param minDisponibles  numero de unidades minimas que el almacen debería de
   *                        tener.
   * 
   *                        Las colas son threat-safe.
   * @apiNote Todo producto tiene que ser accedido a través de una {@link Peticion
   *          petición}, por lo que al llamar a un método que pueda acceder a la
   *          sección crítica del producto requiere de una petición como
   *          parámetro.
   * @category
   *           Clase auxiliar 1/2.
   * 
   */
  private class Producto {
    private volatile String id;
    private Monitor.Cond reabastecerCond, entregarCond;
    private ConcurrentLinkedQueue<Peticion> colaPeticiones;
    private volatile int disponibles, enCamino, comprados;
    private final int minDisponibles;

    /**
     * Constructor de la clase Producto
     * 
     * @param id
     * @param min
     */
    private Producto(String id, int min) {
      this.id = id;
      this.disponibles = 0;
      this.enCamino = 0;
      this.comprados = 0;
      minDisponibles = min;
      colaPeticiones = new ConcurrentLinkedQueue<>();
      reabastecerCond = mon.newCond();
      entregarCond = mon.newCond();
    }

    /**
     * Crea los productos a partir de mapa original y los añade al que usaremos.
     * 
     * @param map
     * @return
     */
    public ConcurrentHashMap<String, Producto> fromMap(Map<String, Integer> map, ControlAlmacenMonitor ths) {
      ConcurrentHashMap<String, Producto> res = new ConcurrentHashMap<>();
      map.forEach((str, min) -> {
        res.put(str, ths.new Producto(str, min));
      });
      return res;
    }

    ////////////////////////
    // Funciones del CTAD /////////////////////////////
    ////////////////////////

    /**
     * Funcion auxiliar de COMPRAR
     * 
     * @param request
     * @return true si ha podido hacer la compra o false si ha fallado
     */
    public boolean hacerPedido(Peticion request) {
      mon.enter(); // Entra en el monitor principal
      if (!this.disponible(request.getCantidad())) { // No se cumple la condición para comprar, devolver false.
        System.out.println("No se pudo hacer el pedido. Por favor intentelo de nuevo más tarde...");
        mon.leave();
        return false;
      } else { // Devolvar true, añadir cantidad de peticion a comprados.
        this.comprados += request.getCantidad();
        tryUnlock(colaPeticiones.peek());
        mon.leave();
        return true;
      }
    }

    /**
     * Funcion auxiliar de OFRECERREABASTECER
     * 
     * @param request
     */
    public void solicitarReabastecer(Peticion request) {
      mon.enter(); // Entra en el monitor general
      if (!CPREofrecerReabastecer()) // Si no es necesario, espera a un signal
        reabastecerCond.await();
      this.enCamino += request.getCantidad(); // Marca como "en camino" las unidades de la request
      tryUnlock(colaPeticiones.peek());
      mon.leave();
    }

    /**
     * Funcion auxiliar de REABASTECER
     * 
     * @param request
     */
    public void reabastecerProd(Peticion request) {
      mon.enter(); // Entra al monitor general
      this.enCamino -= request.getCantidad(); // Cambia la cantidad de la request de en camino a disponibles
      this.disponibles += request.getCantidad();
      tryUnlock(colaPeticiones.peek());
      mon.leave();
    }

    /**
     * Funcion auxiliar de DEVOLVER
     * 
     * @param request
     */
    public void solicitardevolucion(Peticion request) {
      mon.enter();
      this.disponibles += request.getCantidad();
      tryUnlock(colaPeticiones.peek());
      mon.leave();
    }

    /**
     * Funcion auxiliar de Entregar
     * 
     * @param request
     */
    public void hacerEntrega(Peticion request) {
      mon.enter();
      if (entregarCond.waiting() > 0 || !CPREentregar(request)) {
        colaPeticiones.add(request);
        entregarCond.await();
        hacerEntregaYa(colaPeticiones.poll());
        mon.leave();
        return;
      }
      comprados -= request.getCantidad();
      disponibles -= request.getCantidad();
      tryUnlock(colaPeticiones.peek());
      mon.leave();
    }

    /////////////////////////
    // Funciones auxiliares ///////////////////////////////
    /////////////////////////

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

    /**
     * Desbloqueador genérico.
     * 
     * @apiNote Primero desbloquea
     *          {@link ControlAlmacenMonitor#entregar(String, String, int)
     *          entregar}. y tras finalizar la operación anterior desbloqueará
     *          {@link ControlAlmacenMonitor#ofrecerReabastecer(String, int)
     *          ofrecerReabastecer}
     * 
     */
    private void tryUnlock(Peticion pet) {
      if (entregarCond.waiting() > 0 && pet != null && CPREentregar(pet))
        entregarCond.signal();
      else if (reabastecerCond.waiting() > 0 && CPREofrecerReabastecer())
        reabastecerCond.signal();
    }

    /**
     * Hace la entrega inmediatamente, se presupone que se cumple la CPRE y no se comprueba.
     * @param request
     */
    private void hacerEntregaYa(Peticion request) {
      mon.enter();
      if (request == null) {
        tryUnlock(null);
        mon.leave();
      return;
      }
      comprados -= request.getCantidad();
      disponibles -= request.getCantidad();
      tryUnlock(colaPeticiones.peek());
      mon.leave();
    }

    /**
     * CPRE de {@link ControlAlmacenMonitor#ofrecerReabastecer(String, int)
     * ofrecerReabastecer} (self(p).disponibles + self(p ). enCamino −
     * self(p).comprados < self(p).minDisponibles).
     * 
     * @return true si la CPRE se cumple.
     */
    private boolean CPREofrecerReabastecer() {
      return !disponible(minDisponibles);
    }

    /**
     * CPRE de {@link ControlAlmacenMonitor#entregar(String, String, int) entregar}
     * (self(p).disponibles ≥ n).
     * 
     * @param request {@code peticion} de la entrega.
     * @return true si la CPRE se cumple.
     */
    private boolean CPREentregar(Peticion request) {
      return request.getCantidad() <= disponibles;
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }
  }

  ///////////////////////////////
  /**
   * Clase que representa una petición. Todo producto requiere de una petición
   * pasada como parámetro en los métodos que tengan acceso a sección crítica.
   * 
   * @param idCliente {@code String} identificativa del cliente.
   * @param cantidad  Cantidad a comprar en esta petición.
   * 
   * @apiNote Esta clase existe únicamente por legibilidad para la lectura del
   *          código. Podría sustituirse por un String id y un int cantidad como
   *          parámetros en los métodos de {@link Producto} que acceden a sección
   *          crítica.
   */
  private class Peticion {
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

  public ControlAlmacenMonitor(Map<String, Integer> tipoProductos) {
    mon = new Monitor();
    mapaProductos = new Producto("", 0).fromMap(tipoProductos, this);
    // Era un método static, el tester no lo admitía...
  }

  /**
   * <ul>
   * <li>
   * <h4>Quick link:</h4>
   * <ul>
   * <li>{@link Producto#hacerPedido(Peticion)}</li>
   * <li>{@link Peticion}</li>
   * </ul>
   * </li>
   * </ul>
   */
  public boolean comprar(String clientId, String itemId, int cantidad) {
    Peticion p = new Peticion(clientId, cantidad);
    if (!mapaProductos.containsKey(itemId))
      throw new IllegalArgumentException(
          "ERR: El producto introducido no es válido. Ningún producto del almacén concuerda con el id.");
    return mapaProductos.get(itemId).hacerPedido(p);
  }

  /**
   * <ul>
   * <li>
   * <h4>Quick link:</h4>
   * <ul>
   * <li>{@link Producto#hacerEntrega(Peticion)}</li>
   * <li>{@link Peticion}</li>
   * </ul>
   * </li>
   * </ul>
   */
  public void entregar(String clientId, String itemId, int cantidad) {
    Peticion p = new Peticion(itemId, cantidad);
    if (!mapaProductos.containsKey(itemId)) {
      throw new IllegalArgumentException(
          "ERR: El producto introducido no es válido. Ningún producto del almacén concuerda con el id.");
    }
    mapaProductos.get(itemId).hacerEntrega(p);
  }

  /**
   * <ul>
   * <li>
   * <h4>Quick link:</h4>
   * <ul>
   * <li>{@link Producto#solicitardevolucion(Peticion)}</li>
   * <li>{@link Peticion}</li>
   * </ul>
   * </li>
   * </ul>
   */
  public void devolver(String clientId, String itemId, int cantidad) {
    Peticion p = new Peticion(itemId, cantidad);
    if (!mapaProductos.containsKey(itemId))
      throw new IllegalArgumentException(
          "ERR: El producto introducido no es válido. Ningún producto del almacén concuerda con el id.");
    mapaProductos.get(itemId).solicitardevolucion(p);
  }

  /**
   * <ul>
   * <li>
   * <h4>Quick link:</h4>
   * <ul>
   * <li>{@link Producto#solicitarReabastecer(Peticion)}</li>
   * <li>{@link Peticion}</li>
   * </ul>
   * </li>
   * </ul>
   */
  public void ofrecerReabastecer(String itemId, int cantidad) {
    Peticion p = new Peticion(itemId, cantidad);
    if (!mapaProductos.containsKey(itemId))
      throw new IllegalArgumentException(
          "ERR: El producto introducido no es válido. Ningún producto del almacén concuerda con el id.");
    mapaProductos.get(itemId).solicitarReabastecer(p);
  }

  /**
   * <ul>
   * <li>
   * <h4>Quick link:</h4>
   * <ul>
   * <li>{@link Producto#reabastecerProd(Peticion)}</li>
   * <li>{@link Peticion}</li>
   * </ul>
   * </li>
   * </ul>
   */
  public void reabastecer(String itemId, int cantidad) {
    Peticion p = new Peticion(itemId, cantidad);
    if (!mapaProductos.containsKey(itemId))
      throw new IllegalArgumentException(
          "ERR: El producto introducido no es válido. Ningún producto del almacén concuerda con el id.");
    mapaProductos.get(itemId).reabastecerProd(p);
  }

}
