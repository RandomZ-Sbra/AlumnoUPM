package aed.Laboratorios.polinomios;

import java.util.Comparator;
import java.util.function.BiConsumer;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.*;

/**
 * Operaciones sobre polinomios de una variable con coeficientes enteros.
 * 
 * @version 0.5
 * @author Departamento LSIIS de la Escuela Técnica Superior de Ingenieros
 *         Informáticos, <a href=
 *         "http://www.upm.es/UPM/Centros/CampusMontegancedo/ETSIngenierosInformaticos">
 *         Universidad Politécnica de Madrid</a>.
 * @author LMD
 * @author S0mbra
 * 
 * @apiNote Esta clase utiliza métodos de la librería <a href=
 *          "https://costa.ls.fi.upm.es/teaching/aed/docs/aedlib/">aedlib</a>,
 *          que es propiedad enteramente creada por el Departamento
 *          de Lenguajes y Sist. Informáticos e Ingeniería del Software de la
 *          Escuela Técnica Superior de Ingenieros Informáticos,
 *          UPM. Ha sido modificada desde su versión 0.1 y propiamente
 *          documentada desde la 0.5 por LMD y S0mbra.
 */
public class Polinomio {

  // Una lista de monomios
  PositionList<Monomio> terms;

  /**
   * Crea el polinomio "0".
   */
  public Polinomio() {
    terms = new NodePositionList<>();
  }

  /**
   * Crea un polinomio definado por una lista con monomios.
   * 
   * @param terms una lista de monomios
   */
  public Polinomio(PositionList<Monomio> terms) {
    this.terms = terms;
  }

  /**
   * Limpia una {@code String} de tal forma que quita los espacios en blanco y
   * deja sólo la secuencia de caracteres además de sustituir los valores suma "+"
   * por caracteres que sean separables. En el caso de {@code Polinomio(String)}
   * sustituye los valores de "+" por "#" y "^" por "&".
   * <p>
   * 
   * <pre>
   * //Ejemplos:
   * cleanString("2+ 5 + -4+3")     //=>"2#5#-4#3"
   * cleanString(" x^2 +   7x +-2") //=>"x&2#7x#-2"
   * </pre>
   * 
   * @param toClean String para depurar.
   * @return Una nueva {@code String} depurada.
   * 
   * @since 0.1
   * 
   * @see <a
   *      href=https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html>java.lang.String</a>
   *      documentation.
   */
  private static String cleanString(String toClean) { // Por estética del código lo he separado en 3 partes:
    String result = toClean.trim().replace(" ", ""); // depurar espacios.
    result = result.replace("+", "#").replace("\u005E", "&"); // depurar caracteres.
    return result; // Devolver resultado.
  }

  /**
   * Método auxiliar genérico que ejecuta un código (función) dado por el
   * parámetro {@code toDo} que
   * derivará cambios en función de un {@code Integer} dado.
   * <p>
   * <h3>Si el int es:</h3>
   * <ul>
   * <li>
   * 
   * <pre>{@code int comparator > 0; => BiConsumer}</pre>
   * 
   * se ejecuta con el último par {@code Pair<E, E>} con un elemento {@code null}
   * en la posición derecha.
   * <li>
   * 
   * <pre>{@code int comparator < 0; => BiConsumer}</pre>
   * 
   * se ejecuta con el último par {@code Pair<E, E>} con un elemento {@code null}
   * en la posición izquierda.
   * <li>
   * 
   * <pre>{@code int comparator == 0; => BiConsumer}</pre>
   * 
   * se ejecuta con el último par {@code Pair<E, E>} con todos los elementos
   * insertados.
   * </ul>
   * 
   * @param <E>        Preferiblemente un elemento de la clase {@code Monomio}
   * @param elements1  Lista sobre la que se ejecuta el cambio.
   * @param elements2  Lista secundaria con elementos para comparar.
   * @param pointer1   Puntero de la lista principal.
   * @param pointer2   Puntero de la lista secundaria.
   * @param comparator Decide los parámetros de la función a ejecutar.
   * @param toDo       Función implementada para ejecutar depende de
   *                   cadasituación.
   * @return Un número {@code Integer} que corresponde a la función ejecutada
   *         dependiendo del {@code parámetro comparator}.
   *         <p>
   *         Si {@code comparator} == 0 entonces devuelve 0.
   *         <p>
   *         Si {@code comparator} > 0 entonces devuelve 1.
   *         <p>
   *         Si {@code comparator} < 0 entonces devuelve 2.
   * 
   * @since 0.3
   * 
   * @see <a
   *      href=
   *      "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/BiConsumer.html">
   *      java util function BiConsumer<E, T> <a>
   * @implNote
   *           Se espera que se use derivando un cambio en las listas dadas
   *           ({@code elements1} y/o {@code elements2}).
   *           <p>
   *           La función genérica no
   *           realiza ninguna acción, únicamente devuelve un valor entero que
   *           varía entre 0 y 2, el usuario debe darle sentido a dichos valores
   *           pero deberá hacerlo almacenando el valor en una variable. De lo
   *           contrario muchas llamadas a este método derivará en cambiar las
   *           listas dadas tantas veces como se le llame.
   */
  private static <E> int insertion(PositionList<E> elements1, PositionList<E> elements2, Position<E> pointer1,
      Position<E> pointer2, int comparator, BiConsumer<Pair<PositionList<E>, Position<E>>, Pair<E, E>> toDo) {
    if (comparator == 0) {
      toDo.accept(new Pair<>(elements1, pointer1), new Pair<>(pointer1.element(), pointer2.element()));
      return 0;
    } else if (comparator > 0) {
      toDo.accept(new Pair<>(elements1, pointer1), new Pair<>(pointer1.element(), null));
      return 1;
    } else {
      toDo.accept(new Pair<>(elements1, pointer1), new Pair<>(null, pointer2.element()));
      pointer2 = elements2.next(pointer2);
      return 2;
    }
  }

  /**
   * Comprueba si una posición dada es cero.
   * 
   * @param pos posición a comparar; contiene un {@link Monomio}.
   * @return un valor booleano. {@code True} si el elemento {@code pos} contiene
   *         un 0 en el coeficiente, {@code false} en cualquier otro caso.
   * @since 0.4
   */
  private static boolean isCero(Position<Monomio> pos) {
    if (pos.element().getCoeficiente() == 0)
      return true;
    return false;
  }

  /**
   * Método para eliminar {@link Monomio monomios} de un {@link Polinomio
   * polinomio} cuyo coeficiente sea {@code 0}.
   * 
   * @param p polinomio para limpiar
   * @return el <i>mismo</i> polinomio sin ceros en el coeficiente de sus
   *         monomios.
   * @since 0.4
   */
  private static Polinomio cleanCeros(Polinomio p) {
    if (p != null && !p.terms.isEmpty()) {
      Position<Monomio> pointer = p.terms.first();
      while (pointer != null) {
        if (isCero(pointer)) {
          Position<Monomio> auxPos = p.terms.next(pointer);
          p.terms.remove(pointer);
          pointer = auxPos;
        } else {
          pointer = p.terms.next(pointer);
        }
      }
    }
    return p;
  }

  /**
   * {@inheritDoc super()}
   * 
   * @since 0.5
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    Polinomio aux = (Polinomio) this;
    Polinomio result = new Polinomio();
    if (result.equals(aux)) {
      return result;
    }
    result.terms.addFirst(
        new Monomio(aux.terms.first().element().getCoeficiente(), aux.terms.first().element().getExponente()));
    Position<Monomio> auxPointer = aux.terms.next(aux.terms.first());
    Position<Monomio> resPointer = result.terms.first();
    while (auxPointer != null) {
      result.terms.addAfter(resPointer,
          new Monomio(auxPointer.element().getCoeficiente(), auxPointer.element().getExponente()));
      auxPointer = aux.terms.next(auxPointer);
      resPointer = result.terms.next(resPointer);
    }
    return result;
  }

  /**
   * {@code Comparator} de {@code Monomio} final que devuelve un valor entre [-1,
   * 1] para definir el
   * comportamiento de una función más adelante.
   * <p>
   * Si ambos Monomios
   * 
   * @since 0.3
   * 
   * @see {@link Polinomio#insertion(PositionList, PositionList, Position, Position, int, BiConsumer)
   *      insertion(...)}
   *      <li><a href=
   *      "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Comparator.html">
   *      Comparator<T> </a>
   */
  private static final Comparator<Monomio> compGrado = (m1, m2) -> {
    if (m1 == null && m2 == null) {
      throw new RuntimeException("Both values were null");
    } else if (m1 != null && m2 == null) {
      return 1;
    } else if (m1 == null && m2 != null) {
      return -1;
    }
    if (m1.getExponente() == m2.getExponente()) {
      return 0;
    } else if (m1.getExponente() < m2.getExponente()) {
      return -1;
    } else {
      return 1;
    }
  };

  /**
   * Función o código a ejecutar.
   * <h4>Contiene:</h4>
   * <ul>
   * <li>Una {@code lista}.
   * <li>Un {@code puntero} para apuntar en la lista.
   * <li>Dos elementos {@code Monomio} para compararse entre sí y definir su
   * posición en la {@code lista} con el puntero de referencia.
   * </ul>
   * 
   * @since 0.3
   * 
   * @see {@link Polinomio#insertion(PositionList, PositionList, Position,
   *      Position, int, BiConsumer) insertion(...)}
   *      <li><a href=
   *      "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/BiConsumer.html">BiConsumer<E,
   *      T>
   *      </a>
   * @implNote
   *           Notar que para cada uso se debe implementar una función/Clase o una
   *           expresión
   *           {@code $lambda} distinta que especifique el comportamiento que la
   *           función
   *           seguirá según los parámetros añadidos.
   */
  private static final BiConsumer<Pair<PositionList<Monomio>, Position<Monomio>>, Pair<Monomio, Monomio>> action = (
      pairNodePoint, pairElemElem) -> {
    if (pairNodePoint.getRight() != null) {
      if (pairElemElem.getRight() == null) {
        return;
      } else if (pairElemElem.getLeft() == null) {
        pairNodePoint.getLeft().addBefore(pairNodePoint.getRight(), pairElemElem.getRight());
      } else {
        int coef = pairElemElem.getLeft().getCoeficiente() + pairElemElem.getRight().getCoeficiente();
        int exp = pairElemElem.getRight().getExponente();
        pairNodePoint.getLeft().set(pairNodePoint.getRight(), new Monomio(coef, exp));
      }
    } else {
      Position<Monomio> auxPos = pairNodePoint.getLeft().last();
      if (pairElemElem.getRight() == null) {
        return;
      } else if (pairElemElem.getLeft() == null) {
        pairNodePoint.getLeft().addAfter(auxPos, pairElemElem.getRight());
      } else {
        int coef = pairElemElem.getLeft().getCoeficiente() + pairElemElem.getRight().getCoeficiente();
        int exp = pairElemElem.getRight().getExponente();
        pairNodePoint.getLeft().set(auxPos, new Monomio(coef, exp));
      }
    }
  };

  /**
   * Crea un polinomio definido por un String.
   * La representación del polinomio es una secuencia de monomios separados
   * por '+' (y posiblemente con caracteres blancos).
   * Un monomio esta compuesto por tres partes,
   * el coefficiente (un entero), el caracter 'x' (el variable), y el exponente
   * compuesto por un un caracter '^' seguido por un entero.
   * Se puede omitir multiples partes de un monomio,
   * ejemplos:
   * 
   * <pre>
   * {@code
   * new Polinomio("2x^3 + 9");
   * new Polinomio("2x^3 + -9");
   * new Polinomio("x"); // == 1x^1
   * new Polinomio("5"); // == 5x^0
   * new Polinomio("8x"); // == 8x^1
   * new Polinomio("0"); // == 0x^0
   * }
   * </pre>
   * 
   * @throws IllegalArgumentException si el argumento es malformado
   * @param polinomio - una secuencia de monomios separados por '+'
   */
  public Polinomio(String polinomio) {
    terms = new NodePositionList<>();
    Position<Monomio> pointer = null;
    polinomio = cleanString(polinomio);
    String[] terminos = polinomio.split("#");
    // ArrayList<Monomio> aux = new ArrayList<>();
    for (int i = 0; i < terminos.length; i++) {
      Integer coeficiente;
      Integer grado = 0;
      if (terminos[i].contains("&"))
        grado = Integer.parseInt(terminos[i].split("&")[1]);
      else if (terminos[i].contains("x"))
        grado = 1;
      try {
        if (terminos[i].contains("x"))
          try {
            coeficiente = Integer.parseInt(terminos[i].split("x")[0]);
          } catch (ArrayIndexOutOfBoundsException e) {
            coeficiente = 1;
          }
        else
          coeficiente = Integer.parseInt(terminos[i]);
      } catch (NumberFormatException e) {
        coeficiente = 1;
      }
      if (terms.isEmpty()) {
        terms.addFirst(new Monomio(coeficiente, grado));
        pointer = terms.first();
      } else {
        if (grado > pointer.element().getExponente()) {
          terms.addBefore(pointer, new Monomio(coeficiente, grado));
        } else {
          terms.addAfter(pointer, new Monomio(coeficiente, grado));
          pointer = terms.next(pointer);
        }
      }
    }
    cleanCeros(this);
  }

  /**
   * Suma dos polinomios.
   * 
   * @param p1 primer polinomio.
   * @param p2 segundo polinomio.
   * @return la suma de los polinomios.
   */
  public static Polinomio suma(Polinomio p1, Polinomio p2) {
    if (!p1.terms.isEmpty() && !p2.terms.isEmpty()) {
      Polinomio result = null;
      try {
        result = (Polinomio) p1.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
        return null;
      }
      Position<Monomio> pointer1 = result.terms.first();
      Position<Monomio> pointer2 = p2.terms.first();
      while (pointer2 != null || pointer1 != null) {
        Monomio m1 = pointer1 != null ? pointer1.element() : null;
        Monomio m2 = pointer2 != null ? pointer2.element() : null;
        int compare = compGrado.compare(m1, m2);
        int carry = insertion(result.terms, p2.terms, pointer1, pointer2, compare, action);
        if (carry == 0) {
          pointer1 = result.terms.next(pointer1);
          pointer2 = p2.terms.next(pointer2);
        } else if (carry == 1) {
          pointer1 = result.terms.next(pointer1);
        } else if (carry == 2) {
          pointer2 = p2.terms.next(pointer2);
        }
      }
      return cleanCeros(result);
    }
    if (p1.terms.isEmpty())
      return cleanCeros(p2);
    return cleanCeros(p1);
  }

  /**
   * Substraccion de dos polinomios.
   * 
   * @param p1 primer polinomio.
   * @param p2 segundo polinomio.
   * @return la resta de los polinomios.
   */
  public static Polinomio resta(Polinomio p1, Polinomio p2) {
    if (p2 != null && !p2.terms.isEmpty()) {
      Position<Monomio> pointer2 = p2.terms.first();
      while (pointer2 != null) {
        p2.terms.set(pointer2, new Monomio(-pointer2.element().getCoeficiente(), pointer2.element().getExponente()));
        pointer2 = p2.terms.next(pointer2);
      }
      return suma(p1, p2);
    } else {
      return p1;
    }
  }

  /**
   * Calcula el producto de un monomio y un polinomio.
   * 
   * @param m el monomio
   * @param p el polinomio
   * @return el producto del monomio y el polinomio
   */
  public static Polinomio multiplica(Polinomio p1, Polinomio p2) {
    if (!p1.terms.isEmpty() && !p2.terms.isEmpty()) {
      Position<Monomio> pointer1 = p1.terms.first();
      Polinomio result = new Polinomio();
      while (pointer1 != null) {
        result = suma(result, multiplica(pointer1.element(), p2));
        pointer1 = p1.terms.next(pointer1);
      }
      return cleanCeros(result);
    } else {
      return new Polinomio();
    }
  }

  /**
   * Calcula el producto de dos polinomios.
   * 
   * @param p1 primer polinomio.
   * @param p2 segundo polinomio.
   * @return el producto de los polinomios.
   */
  public static Polinomio multiplica(Monomio t, Polinomio p) {
    if (!p.terms.isEmpty() && t.getCoeficiente() != 0) {
      BiConsumer<Pair<PositionList<Monomio>, Position<Monomio>>, Pair<Monomio, Monomio>> toDo = (nodePos, elemElem) -> {
        int coef = elemElem.getRight().getCoeficiente() * elemElem.getLeft().getCoeficiente();
        int exp = elemElem.getRight().getExponente() + elemElem.getLeft().getExponente();
        nodePos.getLeft().addBefore(nodePos.getRight(), new Monomio(coef, exp));
      };
      Polinomio result = new Polinomio();
      result.terms.addFirst(t);
      Position<Monomio> pointer = p.terms.first();
      Position<Monomio> auxPointer = result.terms.first();
      while (pointer != null) {
        insertion(result.terms, null, auxPointer, pointer, 0, toDo);
        pointer = p.terms.next(pointer);
      }
      result.terms.remove(result.terms.last());
      return cleanCeros(result);
    } else {
      return new Polinomio();
    }
  }

  /**
   * Devuelve el valor del polinomio cuando su variable es sustiuida por un valor
   * concreto.
   * Si el polinomio es vacio (la representacion del polinomio "0") entonces
   * el valor devuelto debe ser -1.
   * 
   * @param valor el valor asignado a la variable del polinomio
   * @return el valor del polinomio para ese valor de la variable.
   */
  public long evaluar(int valor) {
    if (this.terms.isEmpty()) {
      return 0;
    }
    long result = 0;
    Position<Monomio> pointer = this.terms.first();
    while (pointer != null) {
      result += pointer.element().getCoeficiente() * Math.pow(valor, pointer.element().getExponente());
      pointer = this.terms.next(pointer);
    }
    return result;
  }

  /**
   * Devuelve el exponente (grado) del monomio con el mayor grado
   * dentro del polinomio
   * 
   * @return el grado del polinomio
   */
  public int grado() {
    if (!terms.isEmpty())
      return terms.first().element().getExponente();
    return -1;
  }

  @SuppressWarnings("all")
  @Override
  public String toString() {
    if (terms.isEmpty())
      return "0";
    else {
      StringBuffer buf = new StringBuffer();
      Position<Monomio> cursor = terms.first();
      while (cursor != null) {
        Monomio p = cursor.element();
        int coef = p.getCoeficiente();
        int exp = p.getExponente();
        buf.append(new Integer(coef).toString());
        if (exp > 0) {
          buf.append("x");
          buf.append("^");
          buf.append(new Integer(exp)).toString();
        }
        cursor = terms.next(cursor);
        if (cursor != null)
          buf.append(" + ");
      }
      return buf.toString();
    }
  }

  @Override
  public boolean equals(Object obj) {
    Polinomio toCompare;
    if (obj instanceof Polinomio) {
      toCompare = (Polinomio) obj;
    } else if (obj instanceof Integer) {
      toCompare = new Polinomio();
      toCompare.terms.addFirst(new Monomio((Integer) obj, 0));
    } else {
      return false;
    }
    if (!this.terms.isEmpty() && !toCompare.terms.isEmpty()) {
      Position<Monomio> pointer = this.terms.first();
      Position<Monomio> toCompPoint = toCompare.terms.first();
      boolean isEqual = true;
      while ((pointer != null || toCompPoint != null) && isEqual) {
        isEqual = pointer.element().getCoeficiente() == toCompPoint.element().getCoeficiente()
            && pointer.element().getExponente() == toCompPoint.element().getExponente();
        pointer = this.terms.next(pointer);
        toCompPoint = toCompare.terms.next(toCompPoint);
      }
      return isEqual;
    } else if (this.terms.isEmpty()) {
      return toCompare.terms.isEmpty();
    } else {
      return this.terms.isEmpty();
    }
  }
}
