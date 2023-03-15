package aed.Laboratorios.hashtable;

import java.util.function.Predicate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Comparator;

import es.upm.aedlib.Entry;
import es.upm.aedlib.EntryImpl;
import es.upm.aedlib.map.Map;
import es.upm.aedlib.InvalidKeyException;

/**
 * A hash table implementing using open addressing to handle key collisions.
 */
public class HashTable<K, V> implements Map<K, V> {
  private Entry<K, V>[] buckets;
  private int size;

  public HashTable(int initialSize) {
    this.buckets = createArray(initialSize);
    this.size = 0;
  }

  /*
   * Add here the method necessary to implement the Map api, and
   * any auxilliary methods you deem convient.
   */

  /**
   * Método auxiliar que devuelve la posición más cercana de una array desde una
   * posición {@code form} que cumple con la condición del predicado dado.
   * 
   * @param <E>       Elemento genérico.
   * @param arr       {@code Array} sobre la que se realiza la búsqueda.
   * @param from      {@code Integer} desde donde la búsqueda doble comienza,
   *                  buscará a través de los índices más bajos (hacia la
   *                  izquierda) y los más altos (hacia la derecha)
   *                  simultáneamente parando cuando se encuentra el primer
   *                  elemento.
   * @param condition <i>Predicado</i> que indica la condición por la cual el
   *                  elemento de la array cumple la especificación para dejar de
   *                  iterar y devolver un resultado.
   * @return La posición más cercana a {@code int from} que cumple con la
   *         condición. Si dos posiciones se encuentran a la misma distancia, la
   *         que tenga el índice más alto (más a la derecha) prevalecerá. Si no
   *         existe ningún elemento que cumpla la condición dada por
   *         {@code Predicate<E> condition} devuelve la posición del elemento nulo
   *         más cercano, a menos que {@code arr} esté llena (sin elementos
   *         nulos), entonces devuelve -1.
   * 
   * @see <a href=
   *      "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/Predicate.html">java.util.function.Predicate<a>
   *      documentation.
   */
  private <E> int doubleIndexSearch(E[] arr, int from, Predicate<E> condition) {
    boolean left = false;
    boolean right = false;
    int nullElem = -1;
    int i = from, j = from;
    while (!left && !right && (i < buckets.length || j >= 0)) {
      if (i < buckets.length) {
        if (nullElem == -1 && arr[i] == null)
          nullElem = i;
        right = condition.test(arr[i]);
        i++;
      }
      if (j >= 0) {
        if (nullElem == -1 && arr[j] == null)
          nullElem = j;
        left = condition.test(arr[j]);
        j--;
      }
    }
    if (right)
      return --i;
    else if (left)
      return ++j;
    else if (nullElem != -1)
      return nullElem;
    else
      return -1;
  }

  // Examples of auxilliary methods: IT IS NOT REQUIRED TO IMPLEMENT THEM

  @SuppressWarnings("unchecked")
  private Entry<K, V>[] createArray(int size) {
    Entry<K, V>[] buckets = new Entry[size];
    return buckets;
  }

  /**
   * Returns the bucket index of an object
   * 
   * @param obj a Key "K"
   * @return
   */
  @SuppressWarnings("unchecked")
  private int index(Object obj) {
    try {
      K key = (K) obj;
      int indx = Math.abs(key.hashCode()) % buckets.length;
      Predicate<Entry<K, V>> condition;
      // int pos = -1;
      if (buckets[indx] == null || !buckets[indx].equals(key)) {
        condition = (e1) -> {
          if (e1 == null)
            return false;
          return e1.getKey().equals(key);
        };
        return doubleIndexSearch(buckets, indx, condition);
      }
      return indx;
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * Returns the index where an entry with the key is located,
   * or if no such entry exists, the "next" bucket with no entry,
   * or if all buckets stores an entry, -1 is returned.
   * 
   * @param obj
   * @return
   */
  private int search(Object obj) {
    int indx = -1;
    boolean checked = false;
    boolean guardadoN = false;
    for (int i = 0; i < buckets.length && !checked; i++) {
      if (buckets[i] != null && buckets[i].getValue().equals(obj)) {
        indx = i;
        checked = true;
      } else if (buckets[i] == null && !guardadoN) {
        indx = i;
        guardadoN = true;
      }
    }
    return indx;
  }

  // Doubles the size of the bucket array, and inserts all entries present
  // in the old bucket array into the new bucket array, in their correct
  // places. Remember that the index of an entry will likely change in
  // the new array, as the size of the array changes.

  private void rehash() {
    Predicate<Entry<K, V>> condition = (e1) -> {
      return e1 == null;
    };
    Comparator<Entry<K, V>> comp = (e1, e2) -> {
      return Math.abs(e1.getKey().hashCode()) - Math.abs(e2.getKey().hashCode());
    };
    Entry<K, V>[] newBuckets = createArray(buckets.length * 2);
    Arrays.sort(buckets, comp);
    for (int i = 0; i < buckets.length; i++) {
      int indx = Math.abs(buckets[i].getKey().hashCode()) % newBuckets.length;
      if (newBuckets[indx] == null)
        newBuckets[indx] = buckets[i];
      else {
        newBuckets[doubleIndexSearch(newBuckets, indx, condition)] = buckets[i];
      }
    }
    buckets = newBuckets;
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    return entries().iterator();
  }

  @Override
  public boolean containsKey(Object arg0) throws InvalidKeyException {
    if (arg0 == null)
      throw new InvalidKeyException();
    Predicate<Entry<K, V>> pred = (e1) -> {
      return e1 != null && e1.getKey().equals(arg0);
    };
    int indx = Math.abs(arg0.hashCode()) % buckets.length;
    int pos = doubleIndexSearch(buckets, indx, pred);
    if (pos == -1)
      return false;
    return buckets[pos] != null;
  }

  @Override
  public Iterable<Entry<K, V>> entries() {
    // PositionList<Entry<K, V>> iterable = new NodePositionList<>(buckets);
    LinkedList<Entry<K, V>> iterable = new LinkedList<>();
    for (int i = 0; i < buckets.length; i++) {
      if (buckets[i] != null)
        iterable.add(buckets[i]);
    }
    return iterable;
  }

  @Override
  public V get(K arg0) throws InvalidKeyException {
    if (arg0 == null)
      throw new InvalidKeyException();
    try {
      return buckets[index(arg0)].getValue();
    } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean isEmpty() {
    Entry<K, V>[] empty = createArray(buckets.length);
    return Arrays.equals(buckets, empty);
  }

  @Override
  public Iterable<K> keys() {
    LinkedList<K> iterable = new LinkedList<>();
    for (int i = 0; i < buckets.length; i++) {
      if (buckets[i] != null)
        iterable.add(buckets[i].getKey());
    }
    return iterable;
  }

  @Override
  public V put(K arg0, V arg1) throws InvalidKeyException {
    V aux = null;
    if (search(new Object()) != -1) { // no esta entero
      if (containsKey(arg0)) {
        aux = remove(arg0);
        buckets[index(arg0)] = new EntryImpl<K, V>(arg0, arg1);
        size++;
      } else {
        buckets[index(arg0)] = new EntryImpl<K, V>(arg0, arg1);
        size++;
      }
    } else if (containsKey(arg0)) {
      aux = remove(arg0);
      buckets[index(arg0)] = new EntryImpl<K, V>(arg0, arg1);
      size++;
    } else {
      rehash();
      put(arg0, arg1);
    }
    return aux;
  }

  @Override
  public V remove(K arg0) throws InvalidKeyException {
    if (arg0 == null)
      throw new InvalidKeyException();
    else if (!containsKey(arg0))
      return null;
    V value = buckets[index(arg0)].getValue();
    buckets[index(arg0)] = null;
    size--;
    return value;
  }

  @Override
  public int size() {
    return this.size;
  }

  public static void main(String[] args) {
    HashTable<Integer, String> map_0 = new HashTable<>(2);
    map_0.put(7, "Alzaga");
  }
}
