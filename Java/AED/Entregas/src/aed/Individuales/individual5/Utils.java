package aed.Individuales.individual5;

import java.util.Iterator;
import java.util.LinkedHashSet;

import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.*;
import es.upm.aedlib.map.*;

public class Utils {
  
  @SuppressWarnings("unchecked")
  public static <E> PositionList<E> deleteRepeated(PositionList<E> l) {
    Position<E> pointer = l.first();
    LinkedHashSet<E> store = new LinkedHashSet<>(l.size());
    while (pointer != null) {
      store.add(pointer.element());
      pointer = l.next(pointer);
    }
    PositionList<E> result = new NodePositionList<>((E[])store.toArray());
    return result;
  }
  
  public static <E> PositionList<E> compactar (Iterable<E> lista) {
    if (lista == null)
      throw new IllegalArgumentException("La lista dada en el parámetro es nula...");
    NodePositionList<E> result = new NodePositionList<>();
    Position<E> pointer;
    Iterator<E> it = lista.iterator();
    if (it.hasNext())
      result.addFirst(it.next());
    else
      return result;
    pointer = result.first();
    while (it.hasNext()) {
      E elem = it.next();
      if (elem == null && pointer.element() == null) {
        
      } else if (elem == null && pointer.element() != null || !elem.equals(pointer.element())) {
        result.addAfter(pointer, elem);
        pointer = result.next(pointer);
      } 
    }
    return result;
  }
  
  public static Map<String,Integer> maxTemperatures(TempData[] tempData) {
    Map<String, Integer> result = new HashTableMap<>();
    for (TempData aux : tempData) {
      if (!result.containsKey(aux.getLocation()))
        result.put(aux.getLocation(), aux.getTemperature());
      else if (result.get(aux.getLocation()) < aux.getTemperature())
        result.put(aux.getLocation(), aux.getTemperature());
    }
    return result;
  }
}