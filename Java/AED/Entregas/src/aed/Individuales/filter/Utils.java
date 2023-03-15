package aed.Individuales.filter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class Utils {

  public static <E> Iterable<E> filter(Iterable<E> d, Predicate<E> pred) {
    if (d == null || pred == null) {
      throw new IllegalArgumentException("\nNullPointerException should be thrown here, but he is sick right now... "
          + "So instead I, IllegalArgumentException, have been called to tell you that an argument is null and it shouldn't be."
          + "\nHave a nice day :)");
    }
    List<E> list = new LinkedList<>();
    Iterator<E> dItr = d.iterator();
    while (dItr.hasNext()) {
      E elem = dItr.next();
      if (elem != null && pred.test(elem)) {
        list.add(elem);
      }
    }
    return list;
  }
}
