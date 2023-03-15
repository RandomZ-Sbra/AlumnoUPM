package aed.Individuales.positionlistiterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.PositionList;

/**
 * Iterador que devuelve elementos de n en n posiciones, si la posición n veces
 * después de la última apunta a un elemento {@code null} entonces se iteran los
 * elementos de uno en uno hasta encontrar el siguiente no nulo más próximo.
 * <p>
 * Se itera sin modificar la lista original dada como parámetro del constructor.
 * 
 * @apiNote Segunda clase {@code NIterator<E>}. Estaversión no contiene una
 *          estructura de
 *          nodos <i>next</i> y un puntero a <i>tail</i> para desplazarse, sino
 *          que usa
 *          la propia lista dada y un puntero {@code pointer}.
 *          <p>
 *          Para esta clase se emplea un método para saltar los elementos nulos
 *          y una
 *          <i>{@code función}</i> para saltar de n en n elementos sobre la
 *          lista.
 * @version 2.0
 * @see <a href=
 *      "https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/function/Function.html">Function</a>
 * @see <a href=
 *      "https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/Iterator.html">Iterator</a>
 * @author Universidad Politécnica de Madrid (Autor original de la librería
 *         <b>aedlib</b>)
 * @author S0mbra
 */
public class NIterator<E> implements Iterator<E> {
  private PositionList<E> list;
  private Position<E> pointer;
  private boolean started;
  private final int jump;
  /**
   * Función que se aplica para iterar de uno en uno por todos los elementos nulos
   * de la lista.
   * <h4>Returns:</h4>
   * <ul>
   * <li>Una {@code Position<E>} con la posición actualizada.
   * </ul>
   * 
   * @deprecated El método es más eficiente ya que esta función debe llamarse
   *             demasiadas veces.
   */
  @SuppressWarnings("unused")
  private Function<Position<E>, Position<E>> nullChecker;

  /**
   * Función que se aplica para saltar n posiciones, usando la variable final
   * {@link NIterator#jump jump}.
   * 
   * @implNote La función se define en el constructor, pero si se desea reescribir
   *           hay que tener en cuenta que siempre usa un parámetro
   *           {@code Position<E>} y siempre devuelve otro {@code Position<E>}, no
   *           aplica al argumento genérico "?" ni tampoco a "Object".
   * @example
   * 
   *          <pre>
   * {@code Position<E> pos = list.first();}
   * {@code pos = jumpChecker.apply(pos)} </pre>
   *          </ul>
   *          <h4>returns:</h4>
   *          <ul>
   *          <li>Una {@code Position<E>} con la posición actualizada.
   *          </ul>
   * @see <a href=
   *      "https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/function/Function.html">Function</a>
   * @see <a href=
   *      "https://costa.ls.fi.upm.es/teaching/aed/docs/aedlib/es/upm/aedlib/Position.html">Position</a>
   *      (aedlib)
   */
  private Function<Position<E>, Position<E>> jumpChecker;

  /**
   * Constructor para el iterador {@link NIterator}.
   * 
   * @param list Lista sobre la que se quiere iterar.
   * @param n    Número de posiciones que se salta por cada llamada al método
   *             {@link NIterator#next() next()} (dejar en 1 si se desea iterar
   *             todos los elementos <b>no nulos</b>)
   */
  public NIterator(PositionList<E> list, int n) {
    if (n < 1) {
      throw new IllegalArgumentException(
          "No se puede iterar en una lista de n en n elementos si n < 1: " + Integer.class.getName());
    }
    this.list = list;
    started = false; // La iteración no ha empezado todavía (empieza cuando se llama por primera vez
                     // a next)...
    jump = n;
    jumpChecker = (p) -> {
      for (int i = 0; i < jump && p != null; i++) {
        p = list.next(p);
      }
      return p;
    };
  }

  /**
   * Método auxiliar que itera de uno en uno los elementos hasta encontrar el
   * primero no nulo después de la posición <i>{@code p}</i>.
   * 
   * @param p posición desde donde se desea comprobar si el elemento es {@code null}.
   * @return Una {@code Position<E>} actualizada con el elemento no nulo.
   */
  private Position<E> nullChecker(Position<E> p) {
    while (p != null && p.element() == null) {
      p = list.next(p);
    }
    return p;
  }

  @Override
  public boolean hasNext() {
    Position<E> auxPointer;
    if (!started) {
      auxPointer = list.first();
      auxPointer = nullChecker(auxPointer);
      if (auxPointer != null)
        return true;
      else
        return false;
    } else {
      auxPointer = jumpChecker.apply(pointer);
      if (auxPointer != null && nullChecker(auxPointer) != null)
        return true;
      else
        return false;
    }
  }

  @Override
  public E next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    if (!started) {
      pointer = list.first();
      started = true;
      pointer = nullChecker(pointer);
      return pointer.element();
    } else {
      pointer = jumpChecker.apply(pointer);
      pointer = nullChecker(pointer);
      return pointer.element();
    }
  }
}