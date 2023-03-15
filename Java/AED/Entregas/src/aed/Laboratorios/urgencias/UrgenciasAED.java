package aed.Laboratorios.urgencias;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import es.upm.aedlib.Entry;
import es.upm.aedlib.Pair;
import es.upm.aedlib.map.HashTableMap;
import es.upm.aedlib.map.Map;
import es.upm.aedlib.priorityqueue.HeapPriorityQueue;
import es.upm.aedlib.priorityqueue.PriorityQueue;

/**
 * Implementación de la clase {@link aed.Laboratorios.aed.urgencias.Urgencias Urgencias} de la
 * librería aedlib.
 * <p>
 * La clase utiliza un Map para agilizar algunos métodos y lograr la menor
 * complejidad posible suponiendo que los HashMaps tienen complejidad O(1) para
 * get y put. Son las 6am y aquí seguimos :) También usa la implementación de
 * HeapPriorityQueue para una menor complejidad al llamar a los métodos enqueue
 * y dequeue.
 * 
 * @version 1.5
 * @author Universidad Politécnica de Madrid ETSInf. (Autor de la librería
 *         aedlib).
 * @author LMD
 * @author S0mbra
 * @see <a href=
 *      "https://costa.ls.fi.upm.es/teaching/aed/docs/aedlib/es/upm/aedlib/priorityqueue/HeapPriorityQueue.html">aedlib
 *      HeapPriorityQueue</a>
 * @see <a href=
 *      "https://costa.ls.fi.upm.es/teaching/aed/docs/aedlib/es/upm/aedlib/map/Map.html">aedlib
 *      Map</a>
 */
public class UrgenciasAED implements Urgencias {
    /**
     * Montículo que guarda los {@link Entry Entry<K, V>} de la PriorityQueue.
     */
    private Map<String, Entry<Paciente, Integer>> heap;
    /**
     * Cola de prioridad más óptima (su implementación como HeapPriorityQueue). Aquí
     * se actualizan los pacientes y se les asigna una posición según su prioridad
     * por su comparador por defecto: {@code "compareTo"}.
     */
    private PriorityQueue<Paciente, Integer> queue;
    /**
     * Tiempo total de espera de los pacientes QUE SON ATENDIDOS.
     */
    private int totalWaitTime;
    /**
     * Número total de pacientes atendidos
     */
    private int totalAttendedPatients;

    /**
     * Constructor para inicializar cada parámetro.
     */
    public UrgenciasAED() {
        heap = new HashTableMap<>();
        queue = new HeapPriorityQueue<>();
        totalWaitTime = 0;
        totalAttendedPatients = 0;
    }

    @Override
    public Paciente admitirPaciente(String DNI, int prioridad, int hora) throws PacienteExisteException {
        if (heap.get(DNI) != null) {
            throw new PacienteExisteException();
        }
        Paciente patient = new Paciente(DNI, prioridad, hora, hora);
        heap.put(DNI, queue.enqueue(patient, patient.hashCode()));
        return patient;
    }

    @Override
    public Paciente salirPaciente(String DNI, int hora) throws PacienteNoExisteException {
        Entry<Paciente, Integer> check = heap.get(DNI);
        if (check == null) {
            throw new PacienteNoExisteException();
        }
        Paciente patient = check.getKey();

        queue.remove(check);
        heap.remove(DNI);
        return patient;
    }

    @Override
    public Paciente cambiarPrioridad(String DNI, int nuevaPrioridad, int hora) throws PacienteNoExisteException {
        Entry<Paciente, Integer> check = heap.get(DNI);
        if (check == null) {
            throw new PacienteNoExisteException();
        }
        Paciente patient = check.getKey();
        if (patient.getPrioridad() != nuevaPrioridad) {
            queue.remove(check);
            patient.setPrioridad(nuevaPrioridad);
            patient.setTiempoAdmisionEnPrioridad(hora);
            heap.put(DNI, queue.enqueue(patient, patient.hashCode()));
        }
        return patient;
    }

    @Override
    public Paciente atenderPaciente(int hora) {
        if (queue.isEmpty()) {
            return null;
        }
        Paciente patient = queue.dequeue().getKey();

        heap.remove(patient.getDNI());
        totalWaitTime += hora - patient.getTiempoAdmision();
        totalAttendedPatients++;
        return patient;
    }

    @Override
    public void aumentaPrioridad(int maxTiempoEspera, int hora) {
        PriorityQueue<Paciente, Integer> newQueue = new HeapPriorityQueue<>();
        for (Entry<String, Entry<Paciente, Integer>> patient : heap) {
            if (hora - patient.getValue().getKey().getTiempoAdmisionEnPrioridad() > maxTiempoEspera
                    && patient.getValue().getKey().getPrioridad() > 0) {
                patient.getValue().getKey().setPrioridad(patient.getValue().getKey().getPrioridad() - 1);
                patient.getValue().getKey().setTiempoAdmisionEnPrioridad(hora);
            }
            heap.put(patient.getKey(), newQueue.enqueue(patient.getValue().getKey(), patient.getValue().getValue()));

        }
        queue = newQueue;
    }

    @Override
    public Iterable<Paciente> pacientesEsperando() {
        Queue<Paciente> queue = new ConcurrentLinkedQueue<>();
        PriorityQueue<Paciente, Integer> newQueue = new HeapPriorityQueue<>(this.queue);
        while (!newQueue.isEmpty()) {
            Entry<Paciente, Integer> patient = newQueue.dequeue();
            queue.add(patient.getKey());
        }
        return queue;
    }

    @Override
    public Paciente getPaciente(String DNI) {
        if (heap.get(DNI) == null)
            return null;
        else
            return heap.get(DNI).getKey();
    }

    @Override
    public Pair<Integer, Integer> informacionEspera() {
        return new Pair<>(totalWaitTime, totalAttendedPatients);
    }
}