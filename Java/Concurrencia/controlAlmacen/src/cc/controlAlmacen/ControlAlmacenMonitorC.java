package cc.controlAlmacen;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.upm.babel.cclib.Monitor;

public class ControlAlmacenMonitorC implements ControlAlmacen {

	// Resource state
	private Map<String, Integer> disponibles, enCamino, comprados, minDisponibles;
	private List<Peticion> peticionesEsperando;

	// Monitors and conditions
	Monitor mutex;
	Monitor.Cond condReabastecer;

	public ControlAlmacenMonitorC(Map<String, Integer> tipoProductos) { // recibe como parametro la minima cantidad de
																		// productos
		minDisponibles = tipoProductos;
		disponibles = new HashMap<String, Integer>();
		enCamino = new HashMap<String, Integer>();
		comprados = new HashMap<String, Integer>();

		Iterator<String> it = minDisponibles.keySet().iterator();
		while (it.hasNext()) {
			String producto = it.next();
			disponibles.put(producto, minDisponibles.get(producto));
			enCamino.put(producto, 0);
			comprados.put(producto, 0);
		}

		mutex = new Monitor();
		condReabastecer = mutex.newCond();
		peticionesEsperando = new ArrayList<>();

	}

	private class Peticion {
		private String id;
		private int unidades;
		private Monitor.Cond c;

		public Peticion(String id, int unidades) {
			this.id = id;
			this.unidades = unidades;
			c = mutex.newCond();
		}
	}

	public boolean comprar(String clientId, String itemId, int cantidad) {
		boolean result = false;
		// Comprobamos la PRE: n > 0 && p pertenece al dom self siendo n = cantidad y p
		// = itemId
		if (cantidad <= 0 || !minDisponibles.containsKey(itemId))
			throw new IllegalArgumentException("No cumple la precondicion");

		// acceso a la seccion critica y codigo de bloqueo
		mutex.enter();

		// codigo de la operacion
		if (disponibles.get(itemId) + enCamino.get(itemId) >= cantidad + comprados.get(itemId)) {
			result = true;
			comprados.put(itemId, comprados.get(itemId) + cantidad);
		}

		// codigo de desbloqueo y salida de la seccion critica
		desbloqueo(itemId);
		mutex.leave();

		return result;
	}

	public void entregar(String clientId, String itemId, int cantidad) {
		// Comprobamos la PRE: n > 0 && p pertenece al dom self siendo n = cantidad y p
		// = itemId
		if (cantidad <= 0 || !minDisponibles.containsKey(itemId))
			throw new IllegalArgumentException("No cumple la precondicion");

		boolean encontrado = false;

		// acceso a la seccion critica y codigo de bloqueo
		mutex.enter();

		for (int i = 0; i < peticionesEsperando.size() && !encontrado; i++) {
			encontrado = peticionesEsperando.get(i).id.equals(itemId);
		}

		if (encontrado || disponibles.get(itemId) < cantidad) {
			// creamos la peticion y la ponemos en la cola
			Peticion pet = new Peticion(itemId, cantidad);
			pet.c.await();
			peticionesEsperando.add(pet);

		}

		// codigo de la operacion
		disponibles.put(itemId, disponibles.get(itemId) - cantidad);
		comprados.put(itemId, comprados.get(itemId) - cantidad);

		// codigo de desbloqueo y salida de la seccion critica
		desbloqueo(itemId);
		mutex.leave();

	}

	public void devolver(String clientId, String itemId, int cantidad) {
		// Comprobamos la PRE: n > 0 && p pertenece al dom self siendo n = cantidad y p
		// = itemId
		if (cantidad <= 0 || !minDisponibles.containsKey(itemId))
			throw new IllegalArgumentException("No cumple la precondicion");

		// acceso a la seccion critica y codigo de bloqueo
		mutex.enter();

		// codigo de la operacion
		disponibles.put(itemId, disponibles.get(itemId) + cantidad);

		// codigo de desbloqueo y salida de la seccion critica
		desbloqueo(itemId);
		mutex.leave();

	}

	public void ofrecerReabastecer(String itemId, int cantidad) {
		// Comprobamos la PRE: n > 0 && p pertenece al dom self siendo n = cantidad y p
		// = itemId
		if (cantidad <= 0 || !minDisponibles.containsKey(itemId))
			throw new IllegalArgumentException("No cumple la precondicion");

		// acceso a la seccion critica y codigo de bloqueo
		mutex.enter();
		if ((disponibles.get(itemId) + enCamino.get(itemId) - comprados.get(itemId)) < minDisponibles.get(itemId))
			condReabastecer.await();

		// codigo de la operacion
		enCamino.put(itemId, enCamino.get(itemId) + cantidad);

		// codigo de desbloqueo y salida de la seccion critica
		desbloqueo(itemId);
		mutex.leave();
	}

	public void reabastecer(String itemId, int cantidad) { // la fabrica entrega los ejemplares del prducto en el
															// almacen
		// Comprobamos la PRE: n > 0 && p pertenece al dom self siendo n = cantidad y p
		// = itemId
		if (cantidad <= 0 || !minDisponibles.containsKey(itemId))
			throw new IllegalArgumentException("No cumple la precondicion");

		// acceso a la seccion critica y codigo de bloqueo
		mutex.enter();

		// codigo de la operacion
		disponibles.put(itemId, disponibles.get(itemId) + cantidad);
		enCamino.put(itemId, enCamino.get(itemId) - cantidad);

		// codigo de desbloqueo y salida de la seccion critica
		desbloqueo(itemId);
		mutex.leave();
	}

	public void desbloqueo(String itemId) {
		boolean desbloqueado = false;
		for (int i = 0; i < peticionesEsperando.size() && peticionesEsperando.size() > 0; i++) {
			if (peticionesEsperando.get(i).id.equals(itemId)) {
				desbloqueado = (disponibles.get(itemId) >= peticionesEsperando.get(i).unidades);
				if (desbloqueado) {
					peticionesEsperando.get(i).c.signal();
					peticionesEsperando.remove(i);
				}
				break;
			}
		}

		if (!desbloqueado && ((disponibles.get(itemId) + enCamino.get(itemId) - comprados.get(itemId)) < minDisponibles
				.get(itemId))) {
			condReabastecer.signal();
		}
	}

}
