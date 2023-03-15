package aed.Laboratorios.hotel;

import java.util.Comparator;

import es.upm.aedlib.indexedlist.*;

/**
 * Implementa el interfaz Hotel, para realisar y cancelar reservas en un hotel,
 * y para realisar preguntas sobre reservas en vigor.
 * @author S0mbra
 * @author LMD
 */
public class MiHotel implements Hotel {
  /**
   * Usa esta estructura para guardar las habitaciones creadas.
   */
  private IndexedList<Habitacion> habitaciones;

  /**
   * Crea una instancia del hotel.
   */
  public MiHotel() {
    // No se debe cambiar este codigo
    this.habitaciones = new ArrayIndexedList<>();
  }

  @Override
  public void anadirHabitacion(Habitacion habitacion) throws IllegalArgumentException {
    Comparator<Habitacion> comp = (o1, o2) -> {
      return o1.compareTo(o2);
    };
    insert(habitacion, habitaciones, comp);
  }

  /**
   * Método auxiliar para obtener el index de un elemento sobre una lista en
   * función de unas reglas especificadas por un {@code comparador}.
   * 
  * @param <E>  Elemento genérico.
   * @param item    Elemento que se desea insertar.
   * @param sortedLlist Lista en la que se desea insertar el elemento {@code E} de forma
   *             ordenada.
   * @param comp {@code Comparador} que contendrá las reglas según las cuales el
   *             elemento se insertará en una posición u otra en la lista.
   * @return el index del elemento {@code item} en la lista o, en caso de no
   *         existir un elemento que cumpla con las especificaciones, un valor
   *         equivalente a {@link java.lang.Integer#MIN_VALUE Integer.MIN_VALUE}.
   * @implNote Se presupone que la lista dada está ordenada según las reglas del
   *           {@code Comparator<E> comp}.
   */
  private static <E> int indexBinarySearch(IndexedList<E> sortedList, E item, Comparator<E> comp) {
    int high = sortedList.size() - 1;
    int low = 0;
    int mid = 0;
    while (low <= high) {
      mid = low + (high - low) / 2;
      int compare = comp.compare(sortedList.get(mid), item);
      if (0 == compare)
        return mid;
      else if (0 > compare)
        low = mid + 1;
      else
        high = mid - 1;
    }
    return Integer.MIN_VALUE;
  }

  /**
   * Método auxiliar para insertar un elemento {@code E} en una
   * {@code lista indexada} cuyo
   * parámetro
   * genérico sea {@code E}, de tal forma que la lista quede <b>ordenada</b> según
   * las reglas de
   * un {@code comparador}.
   * 
   * @implNote Se presupone que la lista ya está ordenada inicialmente según los
   *           parámetros del comparador. El método sólo inserta elementos no
   *           repetidos en la lista.
   * 
   * @param <E>  Elemento genérico.
   * @param e    Elemento que se desea insertar.
   * @param list Lista en la que se desea insertar el elemento {@code E} de forma
   *             ordenada.
   * @param comp {@code Comparador} que contendrá las reglas según las cuales el
   *             elemento se insertará en una posición u otra en la lista.
   * @throws IllegalArgumentException si el elemento ya está en la lista.
   * @see <a href =
   *      https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Comparator.html>
   *      Java Comprator<E> </a>
   *      <p>
   *      <a href=http://costa.ls.fi.upm.es/teaching/aed/docs/aedlib/indexedList>
   *      Listas indexadas </a>
   */
  private static <E> void insert(E e, IndexedList<E> list, Comparator<E> comp) throws IllegalArgumentException {
    int high = list.size() - 1;
    int low = 0;
    int mid = 0;
    if (high >= 0) {
      int compare = 0;
      while (low <= high) {
        mid = low + (high - low) / 2;
        compare = comp.compare(list.get(mid), e);
        if (0 == compare)
          throw new IllegalArgumentException("El elemento ya está en la lista en la posición " + mid);
        else if (0 > compare)
          low = mid + 1;
        else
          high = mid - 1;
      }
      if (compare > 0) {
        list.add(mid, e);
      } else {
        list.add(mid + 1, e);
      }
    } else {
      list.add(mid, e);
    }
  }

  @Override
  public boolean reservaHabitacion(Reserva reserva) {
    Comparator<Habitacion> c = (o1, o2) -> {
      return o1.compareTo(o2);
    };
    int index = indexBinarySearch(habitaciones, new Habitacion(reserva.getHabitacion(), 0), c);
    if (index == Integer.MIN_VALUE)
      throw new IllegalArgumentException("La habitación que desea reservar no existe en este hotel...");
    Comparator<Reserva> comp = new MiComparador();
    try {
      insert(reserva, habitaciones.get(index).getReservas(), comp);
    } catch (IllegalArgumentException e) {
      return false;
    }
    return true;
  }

  @Override
  public boolean cancelarReserva(Reserva reserva) {
    Comparator<Habitacion> c = (o1, o2) -> {
      return o1.compareTo(o2);
    };
    int index = indexBinarySearch(habitaciones, new Habitacion(reserva.getHabitacion(), 0), c);
    if (index == Integer.MIN_VALUE)
      throw new IllegalArgumentException("La habitación que desea reservar no existe en este hotel...");
    return habitaciones.get(index).getReservas().remove(reserva);

  }

  @Override
  public IndexedList<Habitacion> disponibilidadHabitaciones(String diaEntrada, String diaSalida) {
    MiHotel aux = this;
    IndexedList<Habitacion> result = new ArrayIndexedList<>();
    Comparator<Habitacion> prizeComparator = (o1, o2) -> {
      if (o1.getPrecio() == o2.getPrecio()) {
        return 1;
      } else {
        Integer p1 = o1.getPrecio();
        Integer p2 = o2.getPrecio();
        return p1.compareTo(p2);
      }
    };
    for (int i = 0; i < habitaciones.size(); i++) {
      Reserva r = new Reserva(aux.getHabitaciones().get(i).getNombre(), null, diaEntrada, diaSalida);
      if (aux.reservaHabitacion(r)) {
        insert(habitaciones.get(i), result, prizeComparator);
      }
      aux.cancelarReserva(r);
    }
    return result;
  }

  @Override
  public IndexedList<Reserva> reservasPorCliente(String dniPasaporte) {
    IndexedList<Reserva> result = new ArrayIndexedList<>();
    int i = 0;
    Habitacion current;

    while (i < habitaciones.size()) {
      current = habitaciones.get(i);
      if (current.getReservas().size() != 0) {
        for (int j = 0; j < current.getReservas().size(); j++) {
          if (current.getReservas().get(j).getDniPasaporte().equals(dniPasaporte)) {
            result.add(result.size(), current.getReservas().get(j));
          }
        }
      }
      i++;
    }
    return result;
  }

  @Override
  public IndexedList<Habitacion> habitacionesParaLimpiar(String hoyDia) {
    IndexedList<Habitacion> result = new ArrayIndexedList<>();
    Comparator<Habitacion> comp = (o1, o2) -> {
      return o1.compareTo(o2);
    };
    for (int i = 0; i < habitaciones.size(); i++) {
      for (int j = 0; j < habitaciones.get(i).getReservas().size(); j++) {
        if (habitaciones.get(i).getReservas().get(j).getDiaEntrada().compareTo(hoyDia) < 0
            && habitaciones.get(i).getReservas().get(j).getDiaSalida().compareTo(hoyDia) >= 0) {
          insert(habitaciones.get(i), result, comp);
          break;
        }
      }
    }
    return result;
  }

  @Override
  public Reserva reservaDeHabitacion(String nombreHabitacion, String dia) {
    Reserva result = null;
    Comparator<Habitacion> c = (o1, o2) -> {
      return o1.compareTo(o2);
    };
    int index = indexBinarySearch(habitaciones, new Habitacion(nombreHabitacion, 0), c);
    if (index == Integer.MIN_VALUE) {
      throw new IllegalArgumentException(
          "-nadaHabitacion: La reserva que hiciste no fue en una habitación de este hotel");
    } else {
      Habitacion current = habitaciones.get(index);
      //
      Comparator<Reserva> isReserva = (o1, o2) -> {
        if (o1.getDiaEntrada().compareTo(o2.getDiaEntrada()) <= 0
            && o1.getDiaSalida().compareTo(o2.getDiaSalida()) > 0) {
          return 0;
        } else if (o1.getDiaEntrada().compareTo(o2.getDiaEntrada()) > 0) {
          return 1;
        } else {
          return -1;
        }
      };
      int indexRes = indexBinarySearch(current.getReservas(), new Reserva(current.getNombre(), null, dia, dia),
          isReserva);
      if (indexRes != Integer.MIN_VALUE)
        result = current.getReservas().get(indexRes);
      return result;
    }
  }

  public IndexedList<Habitacion> getHabitaciones() {
    return habitaciones;
  }

  /**
   * Clase auxiliar para especificr las reglas para comparar dos reservas según la fecha.
   * @author LMD
   * @author S0mbra
   * @category Comparator class rules.
   * 
   */
  private static class MiComparador implements Comparator<Reserva> {

    @Override
    public int compare(Reserva o1, Reserva o2) {
      if (o1.getDiaEntrada().compareTo(o2.getDiaSalida()) >= 0) {
        return 1;
      } else if (o1.getDiaSalida().compareTo(o2.getDiaEntrada()) <= 0) {
        return -1;
      } else {
        return 0;
      }
    }
  }
}