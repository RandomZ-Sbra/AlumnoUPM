package cc.controlAlmacen;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

// paso de mensajes con JCSP
import org.jcsp.lang.*;

// 
// 
// TODO: otros imports
// 
// 

public class ControlAlmacenCSP implements ControlAlmacen, CSProcess {

	// algunas constantes de utilidad
	static final int PRE_KO = -1;
	static final int NOSTOCK = 0;
	static final int STOCKOK = 1;
	static final int SUCCESS = 0;
	// TODO: añadid las que creáis convenientes

	// canales para comunicación con el servidor
	private final Any2OneChannel chComprar = Channel.any2one();
	private final Any2OneChannel chEntregar = Channel.any2one();
	private final Any2OneChannel chDevolver = Channel.any2one();
	private final Any2OneChannel chOfrecerReabastecer = Channel.any2one();
	private final Any2OneChannel chReabastecer = Channel.any2one();

	// Resource state --> server side !!

	// peticiones de comprar
	private static class PetComprar {
		public String productId;
		public int q;
		public One2OneChannel chresp;

		PetComprar(String productId, int q) {
			this.productId = productId;
			this.q = q;
			this.chresp = Channel.one2one();
		}
	}

	// peticiones de entregar
	private static class PetEntregar {
		public String productId;
		public int q;
		public One2OneChannel chresp;

		PetEntregar(String productId, int q) {
			this.productId = productId;
			this.q = q;
			this.chresp = Channel.one2one();
		}
	}

	// peticiones de devolver
	private static class PetDevolver {
		public String productId;
		public int q;
		public One2OneChannel chresp;

		PetDevolver(String productId, int q) {
			this.productId = productId;
			this.q = q;
			this.chresp = Channel.one2one();
		}
	}

	// para aplazar peticiones de ofrecerReabastecer
	private static class PetOfrecerReabastecer {
		public String productId;
		public int q;
		public One2OneChannel chresp;

		PetOfrecerReabastecer(String productId, int q) {
			this.productId = productId;
			this.q = q;
			this.chresp = Channel.one2one();
		}
	}

	// peticiones de reabastecer
	private static class PetReabastecer {
		public String productId;
		public int q;
		public One2OneChannel chresp;

		PetReabastecer(String productId, int q) {
			this.productId = productId;
			this.q = q;
			this.chresp = Channel.one2one();
		}
	}

	// INTERFAZ ALMACEN
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

	public void entregar(String clientId, String itemId, int cantidad) {
		// petición al servidor
		PetEntregar pet = new PetEntregar(itemId, cantidad);
		chEntregar.out().write(pet);

		// recibimos contestación del servidor
		// puede ser una de {PRE_KO, NOSTOCK, STOCKOK}
		int respuesta = (Integer) pet.chresp.in().read();

		// no se cumple PRE:
		if (respuesta == PRE_KO)
			throw new IllegalArgumentException();
	}

	public void devolver(String clientId, String itemId, int cantidad) {
		// petición al servidor
		PetDevolver pet = new PetDevolver(itemId, cantidad);
		chDevolver.out().write(pet);

		// recibimos contestación del servidor
		// puede ser una de {PRE_KO, NOSTOCK, STOCKOK}
		int respuesta = (Integer) pet.chresp.in().read();

		// no se cumple PRE:
		if (respuesta == PRE_KO)
			throw new IllegalArgumentException();
	}

	public void ofrecerReabastecer(String itemId, int cantidad) {
		// petición al servidor
		PetOfrecerReabastecer pet = new PetOfrecerReabastecer(itemId, cantidad);
		chOfrecerReabastecer.out().write(pet);

		// recibimos contestación del servidor
		// puede ser una de {PRE_KO, NOSTOCK, STOCKOK}
		int respuesta = (Integer) pet.chresp.in().read();

		// no se cumple PRE:
		if (respuesta == PRE_KO)
			throw new IllegalArgumentException();
	}

	public void reabastecer(String itemId, int cantidad) {
		// petición al servidor
		PetReabastecer pet = new PetReabastecer(itemId, cantidad);
		chReabastecer.out().write(pet);

		// recibimos contestación del servidor
		// puede ser una de {PRE_KO, NOSTOCK, STOCKOK}
		int respuesta = (Integer) pet.chresp.in().read();

		// no se cumple PRE:
		if (respuesta == PRE_KO)
			throw new IllegalArgumentException();
	}

	// atributos de la clase
	Map<String, Integer> tipoProductos; // stock mínimo para cada producto

	public ControlAlmacenCSP(Map<String, Integer> tipoProductos) {
		this.tipoProductos = tipoProductos;
		new ProcessManager(this).start(); // al crearse el servidor también se arranca...
	}

	// SERVIDOR
	public void run() {
		ConcurrentHashMap<String, Producto> productos = new Producto(null, 0).fromMap(tipoProductos);
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
		// bucle de servicio
		while (true) {
			// vars. auxiliares
			// tipo de la última petición atendida
			int choice = -1; // una de {COMPRAR, ENTREGAR, DEVOLVER, OFRECER_REABASTECER, REABASTECER}

			// todas las peticiones incluyen un producto y una cantidad
			Producto item = null;
			String pid = "";
			int cantidad = -1;

			choice = servicios.fairSelect();

			switch (choice) {
				case COMPRAR: // CPRE = Cierto
					PetComprar petC = (PetComprar) chComprar.in().read();
					// comprobar PRE:
					pid = petC.productId;
					item = productos.get(pid);
					cantidad = petC.q;
					if (cantidad < 1 || item == null) { // PRE_KO
						petC.chresp.out().write(PRE_KO);
					} else { // PRE_OK
						if (item.disponible(cantidad)) { // hay stock suficiente;
							item.comprados += cantidad;
							petC.chresp.out().write(STOCKOK);
						} else { // no hay stock suficiente
							petC.chresp.out().write(NOSTOCK);
						}
					}
					break;
				case ENTREGAR: // CPRE en diferido
					PetEntregar petE = PetEntregar.class.cast(chEntregar.in().read());
					// comprobar PRE:
					pid = petE.productId;
					item = productos.get(pid);
					cantidad = petE.q;
					if (cantidad < 1 || item == null) { // PRE_KO
						petE.chresp.out().write(PRE_KO);
					} else { // PRE_OK
						if (item.colaPeticiones.isEmpty() && item.disponibles >= cantidad) { // hay stock suficiente;
							item.comprados -= cantidad;
							item.disponibles -= cantidad;
							petE.chresp.out().write(STOCKOK);
						} else { // no hay stock suficiente
							item.colaPeticiones.add(petE);
						}
					}
					break;
				case DEVOLVER: // CPRE = Cierto
					PetDevolver petD = (PetDevolver) chDevolver.in().read();
					// comprobar PRE:
					pid = petD.productId;
					item = productos.get(pid);
					cantidad = petD.q;
					if (cantidad < 1 || item == null) { // PRE_KO
						petD.chresp.out().write(PRE_KO);
					} else { // PRE_OK
						item.disponibles += cantidad;
						petD.chresp.out().write(STOCKOK);
					}
					break;
				case OFRECER_REABASTECER: // CPRE en diferido
					PetOfrecerReabastecer petO = PetOfrecerReabastecer.class.cast(chOfrecerReabastecer.in().read());
					// comprobar PRE:
					pid = petO.productId;
					item = productos.get(pid);
					cantidad = petO.q;
					if (cantidad < 1 || item == null) // PRE_KO
						petO.chresp.out().write(PRE_KO);
					else if (!item.disponible(item.minDisponibles) && item.colaOfrecerReab.isEmpty()) {
						item.enCamino += cantidad;
						petO.chresp.out().write(STOCKOK);
					} else
						item.colaOfrecerReab.add(petO);
					break;
				case REABASTECER: // CPRE = Cierto
					PetReabastecer petR = (PetReabastecer) chReabastecer.in().read();
					// comprobar PRE:
					pid = petR.productId;
					item = productos.get(pid);
					cantidad = petR.q;
					if (cantidad < 1 || item == null) { // PRE_KO
						petR.chresp.out().write(PRE_KO);
					} else { // PRE_OK
						item.disponibles += cantidad;
						item.enCamino -= cantidad;
						petR.chresp.out().write(STOCKOK);
					}
					break;
			} // switch

			// tratamiento de peticiones aplazadas

			// peticiones de entregar
			if (item != null) {
				while (!item.colaPeticiones.isEmpty()
						&& item.colaPeticiones.peek().q <= productos.get(item.id).disponibles) {
					PetEntregar pet = item.colaPeticiones.poll();
					item.disponibles -= pet.q;
					item.comprados -= pet.q;
					pet.chresp.out().write(STOCKOK);
				}
				// peticiones de ofrecer reabastecer
				Producto p = productos.get(item.id);
				while (!item.colaOfrecerReab.isEmpty() && !p.disponible(item.minDisponibles)) {
					PetOfrecerReabastecer pet = item.colaOfrecerReab.poll();
					item.enCamino += pet.q;
					pet.chresp.out().write(STOCKOK);
				}
			}
		} // bucle servicio
	} // run SERVER

	////////////////////
	// Clases auxiliares
	////////////////////

	/**
	 * CPRE de {@link ControlAlmacenMonitor#entregar(String, String, int) entregar}
	 * (self(p).disponibles ≥ n).
	 * 
	 * @param request {@code peticion} de la entrega.
	 * @return true si la CPRE se cumple.
	 */

	/**
	 * Clase estática que simula ser un producto.
	 */
	private static class Producto {
		private final String id; // Identificador único para cada producto, su "nombre".
		private volatile int disponibles; // Actualmente en almacén.
		private volatile int comprados; // Se han asignado
		private volatile int enCamino; // Se ha ofrecido reabastecer
		public final int minDisponibles; // Cantidad mínima que habrá en stock para aceptar una solicitud "reabastecer"
		public ConcurrentLinkedQueue<PetEntregar> colaPeticiones = new ConcurrentLinkedQueue<>();
		private ConcurrentLinkedQueue<PetOfrecerReabastecer> colaOfrecerReab = new ConcurrentLinkedQueue<>();

		/**
		 * Constructor para generar un producto.
		 * 
		 * @param id             el identificador o nombre del producto
		 * @param minDisponibles minimos disponibles para aceptar una solicitud de
		 *                       {@code reabastecer}.
		 */
		public Producto(String id, int minDisponibles) {
			this.id = id;
			this.minDisponibles = minDisponibles;
			disponibles = 0;
			comprados = 0;
			enCamino = 0;
		}

		public ConcurrentHashMap<String, Producto> fromMap(Map<String, Integer> map) {
			ConcurrentHashMap<String, Producto> res = new ConcurrentHashMap<>();
			map.forEach((str, min) -> {
				res.put(str, new Producto(str, min));
			});
			return res;
		}

		/**
		 * Indica si la cantidad pasada como parámetro está disponible teniendo en
		 * cuenta los que hay en camino y restando los ya asignados (comprados).
		 * 
		 * @param cantidad cantidad para comprobar.
		 * @return {@code true} si está en stock o en camino y {@code false} en caso
		 *         contrario.
		 */
		public boolean disponible(int cantidad) {
			if (cantidad <= 0)
				return disponibles + enCamino - comprados > 0;
			return (disponibles + enCamino - comprados) >= cantidad;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Producto)
				return Producto.class.cast(obj).id.equals(this.id);
			return false;
		}

		@Override
		public int hashCode() {
			return id.hashCode();
		}
	}

	public static void main(String[] args) {
		String i1 = "i1";
		String c1 = "c1";
		var a = new ControlAlmacenCSP(Map.of("i1", 1));
		a.ofrecerReabastecer(i1, 1);
		a.reabastecer(i1, 1);
		Thread t = new Thread(() -> {a.ofrecerReabastecer(i1, 4);});
		t.start();
		a.reabastecer(i1, 4);
		Thread t1 = new Thread(() -> {a.ofrecerReabastecer(i1, 4);});
		t1.start();
		a.reabastecer(i1, 4);
		a.comprar(c1, i1, 10);
	}
}
