package aed.Laboratorios.tries;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
//import es.upm.aedlib.tree.BinaryTreeNode;
import es.upm.aedlib.tree.GeneralTree;
import es.upm.aedlib.tree.LinkedGeneralTree;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.positionlist.NodePositionList;

/**
 * Diccionario de palabras con una estructura de árbol. Emplea una metodología
 * similar a la empleada para generar gramáticas a partir alfabetos dados.
 * <p>
 * Los distintos nodos representan un {@linkplain es.upm.aedlib.Pair par} que
 * contendrá un {@code char} y un {@code boolean}. El {@code char} representa un
 * caracter añadido al alfabeto que puede reconocer el diccionario (autómata),
 * mientras que el booleano representa si ese nodo es un estado final o no (que
 * significa que en ese char hay una palabra generada a partir de los nodos
 * anteriores desde la raíz).
 * 
 * @author ETSIInf, Universidad Politécnica de Madrid, DLSIS (creadores de la
 *         librería {@code aedlib} necesaria para el uso de algunas estructuras
 *         de datos).
 * @author LMD
 * @author S0mbra
 * @see <a href="https://costa.ls.fi.upm.es/teaching/aed/docs/aedlib/">aedlib
 *      javadoc</a>.
 */
public class DictImpl implements Dictionary {
  // A boolean because we need to know if a word ends in a node or not
  GeneralTree<Pair<Character, Boolean>> tree;

  public DictImpl() {
    tree = new LinkedGeneralTree<>();
    tree.addRoot(new Pair<Character, Boolean>(null, false));

  }

  /**
   * Devuelve el hijo del nodo pos que contiene el caracter ch.
   */
  private Position<Pair<Character, Boolean>> searchChildLabelledBy(char ch, Position<Pair<Character, Boolean>> pos) {
    if (pos == null) { // Si la posición es nula entonces no existe.
      return null;
    }
    Iterable<Position<Pair<Character, Boolean>>> iterable = tree.children(pos);
    Iterator<Position<Pair<Character, Boolean>>> iter;

    if (iterable != null) {
      iter = iterable.iterator();
    } else {
      return null;
    }

    Position<Pair<Character, Boolean>> res = null;
    boolean found = false;

    while (iter.hasNext() && !found) {
      res = iter.next();
      found = res.element().getLeft().equals(ch);
      if (!found) {
        res = null;
      }
    }
    return res;
  }

  /**
   * Devuelve el nodo cuyo camino desde la raiz contiene
   * la palabra prefix. Si no existe el metodo devuelve null.
   */
  private Position<Pair<Character, Boolean>> findPos(String prefix) {
    if (prefix == null || prefix.isEmpty()) {
      return tree.root();
    }
    boolean noencontrado = false;
    Position<Pair<Character, Boolean>> poscheck = tree.root();

    for (int i = 0; i < prefix.length() && !noencontrado; i++) {
      poscheck = searchChildLabelledBy(prefix.charAt(i), poscheck);
      if (poscheck == null) {
        noencontrado = true;
      }
    }
    return poscheck;
  }

  /**
   * Anade un hijo al nodo pos conteniendo el elemento pair,
   * respetando el orden alfabetico de los hijos.
   */
  private Position<Pair<Character, Boolean>> addChildAlphabetically(Pair<Character, Boolean> pair,
      Position<Pair<Character, Boolean>> pos) {
    Iterable<Position<Pair<Character, Boolean>>> iterable;
    try {
      iterable = tree.children(pos);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      return null;
    } // Si, ya sé que try-catch no son muy rápidos, pero añaden error-handling para
      // cualquier situación inesperada...
    Position<Pair<Character, Boolean>> result = null;
    boolean esta = false;

    if (tree.isInternal(pos)) { // Si está enraizado realizamos el algoritmo de búsqueda
      Iterator<Position<Pair<Character, Boolean>>> iter = iterable.iterator();
      while (iter.hasNext() && !esta) {
        result = iter.next();
        if (result.element().getLeft().compareTo(pair.getLeft()) == 0) {
          esta = true;
          if (!result.element().getRight() && pair.getRight()) {
            tree.set(result, pair);
            result.element().setRight(pair.getRight());
          }
        } else if (result.element().getLeft().compareTo(pair.getLeft()) > 0) {
          result = tree.insertSiblingBefore(result, pair);
          esta = true;
        } else if (!iter.hasNext() && result.element().getLeft().compareTo(pair.getLeft()) < 0) {
          result = tree.insertSiblingAfter(result, pair);
        }
      }
      return result;
    } else { // Si no, entonces sencillamente añadirlo.
      result = tree.addChildFirst(pos, pair);
      return result;
    }
  }

  public void add(String word) {
    if (word == null || word.isEmpty())
      throw new IllegalArgumentException();
    if (!isIncluded(word)) {
      Position<Pair<Character, Boolean>> actual = tree.root();
      Position<Pair<Character, Boolean>> probarnodo = null;
      boolean addNodes = false;
      for (int i = 0; i < word.length(); i++) {
        if (!addNodes) {
          probarnodo = searchChildLabelledBy(word.charAt(i), actual);
          addNodes = probarnodo == null;
        }
        if (addNodes) {
          boolean right = i == word.length() - 1;
          actual = addChildAlphabetically(new Pair<Character, Boolean>(word.charAt(i), right), actual);
        } else {
          actual = probarnodo;
          if (i == word.length() - 1) {
            if (!actual.element().getRight()) {
              actual.element().setRight(true);
              tree.set(probarnodo, actual.element());
            }
          }
        }
      }
    }
  }

  public void delete(String word) {
    if (word == null || word.isEmpty())
      throw new IllegalArgumentException();
    Position<Pair<Character, Boolean>> nodo = findPos(word);
    if (nodo != null) {
      nodo.element().setRight(false);
    }
  }

  public boolean isIncluded(String word) {
    if (word == null || word.isEmpty())
      throw new IllegalArgumentException();
    Position<Pair<Character, Boolean>> nodo = findPos(word);
    return nodo != null && nodo.element().getRight();
  }

  /**
   * Method that returns all the current words that begin with the given
   * {@code prefix}.
   * 
   * @param prefix The prefix that will be searched in order to return the list.
   * @return the implementation of a
   *         {@linkplain es.upm.aedlib.positionlist.PositionList
   *         PositionList<String>} with all the current active words in the
   *         dictionary that begin with the given {@code prefix}.
   */
  public PositionList<String> wordsBeginningWithPrefix(String prefix) {
    Position<Pair<Character, Boolean>> comienzo = tree.root();
    boolean noencontrado = false;
    PositionList<String> result = new NodePositionList<>();
    PositionList<Character> palabra = new NodePositionList<>();

    for (int i = 0; i < prefix.length() && !noencontrado; i++) {
      comienzo = searchChildLabelledBy(prefix.charAt(i), comienzo);
      if (comienzo == null) {
        noencontrado = true;
      } else if (i != prefix.length() - 1)
        palabra.addLast(comienzo.element().getLeft());
    }
    if (noencontrado)
      return null;
    searcherAux(comienzo, result, palabra);
    return result;
  }

  private void searcherAux(Position<Pair<Character, Boolean>> pos, PositionList<String> result,
      PositionList<Character> palabra) {
    if (!pos.equals(tree.root()))
      palabra.addLast(pos.element().getLeft());
    if (!palabra.isEmpty() && pos.element().getRight()) {
      Object[] pal = palabra.toArray();
      char[] primitivePal = Arrays.stream(pal).map(ch -> ch.toString()).collect(Collectors.joining()).toCharArray();
      result.addLast(new String(primitivePal));
    }
    if (tree.isExternal(pos)) {
      palabra.remove(palabra.last());
      return;
    }
    Iterator<Position<Pair<Character, Boolean>>> itr = tree.children(pos).iterator();
    while (itr.hasNext()) {
      Position<Pair<Character, Boolean>> elem = itr.next();
      searcherAux(elem, result, palabra);
    }
    if (!palabra.isEmpty())
      palabra.remove(palabra.last());
    return;
  }
}
