package cc.controlAlmacen;

import org.jcsp.lang.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.LinkedList;

public class ControlAlmacenCSPL implements ControlAlmacen, CSProcess {
	// algunas constantes de utilidad
	static final int PRE_KO = -1;
	static final int NOSTOCK = 0;
	static final int STOCKOK = 1;
	static final int SUCCESS = 0;

	// canales para comunicación con el servidor
	private final Any2OneChannel chComprar = Channel.any2one();
	private final Any2OneChannel chEntregar = Channel.any2one();
	private final Any2OneChannel chDevolver = Channel.any2one();
	private final Any2OneChannel chOfrecerReabastecer = Channel.any2one();
	private final Any2OneChannel chReabastecer = Channel.any2one();

	/**
	 * <b>Peticiones de comprar</b>
	 * 
	 * @param productId Identificador del producto
	 * @param quantity  Cantidad a comprar
	 * @param chresp    Canal de respuesta
	 */
	private static class PetComprar {
		public String productId;
		public int quantity;
		public One2OneChannel chresp;

		PetComprar(String productId, int quantity) {
			this.productId = productId;
			this.quantity = quantity;
			this.chresp = Channel.one2one();
		}
	}

	/**
	 * <b>Peticiones de entregar</b>
	 * 
	 * @param itemId   Identificador del item
	 * @param cantidad Cantidad a entregar
	 * @param chresp   Canal de respuesta
	 */
	private static class PetEntregar {
		public String itemId;
		public int cantidad;
		public One2OneChannel chresp;

		PetEntregar(String ItemId, int cantidad) {
			this.itemId = ItemId;
			this.cantidad = cantidad;
			this.chresp = Channel.one2one();
		}
	}

	/**
	 * <b>Peticiones de devolver</b>
	 * 
	 * @param itemId   Identificador del item
	 * @param cantidad Cantidad a devolver
	 * @param chresp   Canal de respuesta
	 */
	private static class PetDevolver {
		public String itemId;
		public int cantidad;
		public One2OneChannel chresp;

		PetDevolver(String itemId, int cantidad) {
			this.itemId = itemId;
			this.cantidad = cantidad;
			this.chresp = Channel.one2one();
		}
	}

	/**
	 * <b>Peticiones de ofrecerReabastecer</b>
	 * 
	 * @param itemId   Identificador del item
	 * @param cantidad Cantidad a ofrecer para reabastecimiento
	 * @param chresp   Canal de respuesta
	 */
	private static class PetOfrecerReabastecer {
		public String itemId;
		public int cantidad;
		public One2OneChannel chresp;

		PetOfrecerReabastecer(String itemId, int cantidad) {
			this.itemId = itemId;
			this.cantidad = cantidad;
			this.chresp = Channel.one2one();
		}
	}

	/**
	 * <b>Peticiones de reabastecer</b>
	 * 
	 * @param itemId   Identificador del item
	 * @param cantidad Cantidad a reabastecer
	 * @param chresp   Canal de respuesta
	 */
	private static class PetReabastecer {
		public String itemId;
		public int cantidad;
		public One2OneChannel chresp;

		PetReabastecer(String itemId, int cantidad) {
			this.itemId = itemId;
			this.cantidad = cantidad;
			this.chresp = Channel.one2one();
		}
	}

	/**
	 * Clase que representa un ítem en el almacén.
	 * Contiene información sobre la cantidad de ítems disponibles, en camino y
	 * comprados, así como el stock mínimo necesario.
	 */
	private static class MyItem {
		public int disponibles;
		public int enCamino;
		public int comprados;
		public int minDisponibles;

		/**
		 * Constructor de MyItem.
		 * Inicializa las cantidades disponibles, en camino y compradas a 0.
		 * 
		 * @param minDisponibles La cantidad mínima de ítems disponibles requeridos.
		 */
		public MyItem(int minDisponibles) {
			this.disponibles = 0;
			this.enCamino = 0;
			this.comprados = 0;
			this.minDisponibles = minDisponibles;
		}
	}

	// atributos de la clase
	/**
	 * Mapa que almacena los elementos del almacén.
	 * <p>
	 * Cada elemento se identifica por su itemId y el integer del minino disponible
	 */
	Map<String, Integer> tipoProductos; // Stock mínimo para cada producto

	/**
	 * Mapa que almacena los elementos del almacén.
	 * <p>
	 * Cada elemento se identifica por su itemId y tiene un producto (MyItem)
	 * 
	 * @see MyItem
	 */
	Map<String, MyItem> baseDatos = new Hashtable<String, MyItem>();

	/**
	 * <b>Constructor de ControlAlmacenCSP.</b>
	 * 
	 * @param tipoProductos Un mapa de identificadores de productos (clave) y la
	 *                      cantidad mínima disponible (valor).
	 */
	public ControlAlmacenCSPL(Map<String, Integer> tipoProductos) {
		this.tipoProductos = tipoProductos;
		for (String productoId : tipoProductos.keySet()) {
			int minDisponibles = tipoProductos.get(productoId);
			MyItem newItem = new MyItem(minDisponibles);
			baseDatos.put(productoId, newItem);
		}
		new ProcessManager(this).start(); // al crearse el servidor también se arranca...
	}

	// INTERFAZ ALMACEN

	/**
	 * Método para realizar una compra.
	 * 
	 * @param clientId El identificador del cliente que realiza la compra.
	 * @param itemId   El identificador del ítem a comprar.
	 * @param cantidad La cantidad de ítems a comprar.
	 * @return true si la compra se realizó con éxito, false si no hay suficiente stock.
	 * @throws IllegalArgumentException si los parámetros de entrada son inválidos.
	 */
	public boolean comprar(String clientId, String itemId, int cantidad) {

		// petición al servidor
		PetComprar pet = new PetComprar(itemId, cantidad);
		chComprar.out().write(pet);

		// recibimos contestación del servidor
		// puede ser una de {PRE_KO, NOSTOCK, STOCKOK}
		int respuesta = (Integer) pet.chresp.in().read();

		// no se cumple PRE:
		if (respuesta == PRE_KO)
			throw new IllegalArgumentException();
		// se cumple PRE:
		return (respuesta == STOCKOK);
	}

	/**
	 * Método para entregar un ítem.
	 * 
	 * @param clientId El identificador del cliente que realiza la entrega.
	 * @param itemId   El identificador del ítem a entregar.
	 * @param cantidad La cantidad de ítems a entregar.
	 * @throws IllegalArgumentException si los parámetros de entrada son inválidos.
	 */
	public void entregar(String clientId, String itemId, int cantidad) {
		PetEntregar pet = new PetEntregar(itemId, cantidad);
		chEntregar.out().write(pet);
		Integer respuesta = (Integer) pet.chresp.in().read();
		if (respuesta == PRE_KO)
			throw new IllegalArgumentException();

	}

	/**
	 * Método para procesar la devolución de un ítem.
	 * 
	 * @param clientId El identificador del cliente.
	 * @param itemId   El identificador del ítem a devolver.
	 * @param cantidad La cantidad de ítems a devolver.
	 * @throws IllegalArgumentException si los parámetros de entrada son inválidos.
	 */
	public void devolver(String clientId, String itemId, int cantidad) {
		PetDevolver pet = new PetDevolver(itemId, cantidad);
		chDevolver.out().write(pet);
		Integer respuesta = (Integer) pet.chresp.in().read();
		if (respuesta == PRE_KO)
			throw new IllegalArgumentException();
	}

	/**
	 * Método para ofrecer el reabastecimiento de un ítem.
	 * 
	 * @param itemId   El identificador del ítem a reabastecer.
	 * @param cantidad La cantidad de ítems a reabastecer.
	 * @throws IllegalArgumentException si la respuesta es PRE_KO.
	 */
	public void ofrecerReabastecer(String itemId, int cantidad) {
		PetOfrecerReabastecer pet = new PetOfrecerReabastecer(itemId, cantidad);
		chOfrecerReabastecer.out().write(pet);
		Integer respuesta = (Integer) pet.chresp.in().read();
		if (respuesta == PRE_KO)
			throw new IllegalArgumentException();
	}

	/**
	 * Realiza el proceso de reabastecimiento de un ítem.
	 * 
	 * @param itemId   ID del ítem a reabastecer.
	 * @param cantidad Cantidad de ítems a reabastecer.
	 * @throws IllegalArgumentException si la respuesta recibida es PRE_KO.
	 */
	public void reabastecer(String itemId, int cantidad) {
		PetReabastecer pet = new PetReabastecer(itemId, cantidad);
		chReabastecer.out().write(pet);
		Integer respuesta = (Integer) pet.chresp.in().read();
		if (respuesta == PRE_KO)
			throw new IllegalArgumentException();
	}

	/**
	 * Ejecuta el bucle de servicio del servidor, atendiendo diferentes tipos de peticiones.
	 * Este método se ejecuta en un hilo de ejecución separado.
	 */
	public void run() {
		// para recepción alternativa condicional
		Guard[] entradas = {
				chComprar.in(),
				chEntregar.in(),
				chDevolver.in(),
				chOfrecerReabastecer.in(),
				chReabastecer.in()
		};
		Alternative servicios = new Alternative(entradas);

		// OJO ORDEN!!
		final int COMPRAR = 0;
		final int ENTREGAR = 1;
		final int DEVOLVER = 2;
		final int OFRECER_REABASTECER = 3;
		final int REABASTECER = 4;
		// condiciones de recepción: todas a CIERTO

		// Cola de peticiones aplazadas de entrega

		/*
		 * Cola de peticiones aplazadas del canal PetEntregar, usa un mapa para
		 * identificar vincular el nombre del item (Key tipo String)
		 * con un cola de peticiones de entrega (Value).
		 */
		Map<String, LinkedList<PetEntregar>> entregas = new Hashtable<String, LinkedList<PetEntregar>>();
		for (String productoId : tipoProductos.keySet()) {
			entregas.put(productoId, new LinkedList<PetEntregar>());
		}

		// Cola de peticiones aplazadas de ofertas de reabastecimiento

		/*
		 * Cola de peticiones aplazadas del canal PetOfrecerReabastecer, usa un mapa
		 * para identificar vincular el nombre del item (Key tipo String)
		 * con un cola de peticiones de ofertas de reabastecimiento (Value).
		 */
		Map<String, LinkedList<PetOfrecerReabastecer>> reEspera = new Hashtable<String, LinkedList<PetOfrecerReabastecer>>();
		for (String productoId : tipoProductos.keySet()) {
			reEspera.put(productoId, new LinkedList<PetOfrecerReabastecer>());
		}

		// bucle de servicio
		while (true) {
			// variable auxiliar para verificar si existe pid
			boolean existePid = true;
			
			// tipo de la última petición atendida
			int choice = -1; // una de {COMPRAR, ENTREGAR, DEVOLVER, OFRECER_REABASTECER, REABASTECER}

			// todas las peticiones incluyen un producto y una cantidad
			MyItem item = new MyItem(99999);
			String pid = "";
			int cantidad = -1;

			choice = servicios.fairSelect();
			switch (choice) {
				case COMPRAR: // CPRE = Cierto
					PetComprar petC = (PetComprar) chComprar.in().read();
					pid = petC.productId;
					if (!baseDatos.containsKey(pid)) {// PRE_KO
						existePid = false;
						petC.chresp.out().write(PRE_KO);
					} else {
						item = baseDatos.get(pid);
						cantidad = petC.quantity;
						if (cantidad < 1 || item == null) { // PRE_KO
							petC.chresp.out().write(PRE_KO);
						} else { // PRE_OK
							boolean result = item.disponibles + item.enCamino >= cantidad + item.comprados;
							if (result) { // hay stock suficiente
								item.comprados += cantidad;
								baseDatos.put(pid, item);
								petC.chresp.out().write(STOCKOK);
							} else { // no hay stock suficiente
								petC.chresp.out().write(NOSTOCK);
							}
						}
					}
					break;
				case ENTREGAR:// CPRE = Cierto
					PetEntregar petE = (PetEntregar) chEntregar.in().read();
					pid = petE.itemId;
					if (!baseDatos.containsKey(pid)) {// PRE_KO
						existePid = false;
						petE.chresp.out().write(PRE_KO);
					} else {
						item = baseDatos.get(pid);
						cantidad = petE.cantidad;
						if (cantidad < 1 || item == null) { // PRE_KO
							petE.chresp.out().write(PRE_KO);
						} else if (entregas.get(pid).size() > 0 || item.disponibles < cantidad) {
							// ya hay una entrega en espera o esta no puede realizarse todavia
							entregas.get(pid).addLast(petE);
						} else {// entrega aceptada
							item.disponibles -= cantidad;
							item.comprados -= cantidad;
							baseDatos.put(pid, item);
							petE.chresp.out().write(SUCCESS);
						}
					}
					break;
				case DEVOLVER:// CPRE = Cierto
					PetDevolver petD = (PetDevolver) chDevolver.in().read();
					pid = petD.itemId;
					if (!baseDatos.containsKey(pid)) {// PRE_KO
						existePid = false;
						petD.chresp.out().write(PRE_KO);
					} else {
						item = baseDatos.get(pid);
						cantidad = petD.cantidad;
						if (cantidad < 1 || item == null) {// PRE_KO
							petD.chresp.out().write(PRE_KO);
						} else {// devolucion aceptada
							item.disponibles += cantidad;
							baseDatos.put(pid, item);
							petD.chresp.out().write(SUCCESS);
						}
					}
					break;
				case OFRECER_REABASTECER:// CPRE = Cierto
					PetOfrecerReabastecer petOR = (PetOfrecerReabastecer) chOfrecerReabastecer.in().read();
					pid = petOR.itemId;
					if (!baseDatos.containsKey(pid)) {// PRE_KO
						existePid = false;
						petOR.chresp.out().write(PRE_KO);
					} else {
						item = baseDatos.get(pid);
						cantidad = petOR.cantidad;
						if (cantidad < 1 || item == null) {// PRE_KO
							petOR.chresp.out().write(PRE_KO);
						} else if (reEspera.get(pid).size() > 0
								|| item.disponibles + item.enCamino - item.comprados >= item.minDisponibles) {
							// ya hay una oferta de reabastecimiento en espera o esta no debe procesarse
							// todavia
							reEspera.get(pid).addLast(petOR);
						} else {// oferta aceptada
							item.enCamino += cantidad;
							baseDatos.put(pid, item);
							petOR.chresp.out().write(SUCCESS);
						}
					}
					break;
				case REABASTECER:// CPRE = Cierto
					PetReabastecer petR = (PetReabastecer) chReabastecer.in().read();
					pid = petR.itemId;
					if (!baseDatos.containsKey(pid)) {// PRE_KO
						existePid = false;
						petR.chresp.out().write(PRE_KO);
					} else {
						item = baseDatos.get(pid);
						cantidad = petR.cantidad;
						if (cantidad < 1 || item == null) {// PRE_KO
							petR.chresp.out().write(PRE_KO);
						} else {
							// reabastecimiento aceptado
							item.disponibles += cantidad;
							item.enCamino -= cantidad;
							baseDatos.put(pid, item);
							petR.chresp.out().write(SUCCESS);
						}
					}
					break;
			}// switch

			// tratamiento de peticiones aplazadas
			/*
			 * como la ultima accion realizada sera una con el pid del ultimo item,
			 * sabemos que unicamente ha habido cambios en ese elemento.
			 * usamos existePid para verificar sin llamar a baseDatos.containsKey(pid) si el
			 * pid es valido
			 * (ya lo hemos verificado con anterioridad)
			 */
			if (existePid) {
				while (entregas.get(pid).size() > 0
						&& entregas.get(pid).getFirst().cantidad <= baseDatos.get(pid).disponibles) {
					cantidad = entregas.get(pid).getFirst().cantidad;
					baseDatos.get(pid).disponibles -= cantidad;
					baseDatos.get(pid).comprados -= cantidad;
					PetEntregar aux = entregas.get(pid).pop();
					aux.chresp.out().write(SUCCESS);
				}
			}
			if (existePid) {
				while (reEspera.get(pid).size() > 0 && baseDatos.get(pid).disponibles + baseDatos.get(pid).enCamino
						- baseDatos.get(pid).comprados < baseDatos.get(pid).minDisponibles) {
					cantidad = reEspera.get(pid).getFirst().cantidad;
					item.enCamino += cantidad;
					baseDatos.put(pid, item);
					PetOfrecerReabastecer aux = reEspera.get(pid).pop();
					aux.chresp.out().write(SUCCESS);
				}
			}
		} // bucle servicio
	} // run SERVER
}
