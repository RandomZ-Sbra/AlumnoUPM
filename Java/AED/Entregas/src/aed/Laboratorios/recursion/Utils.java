package aed.Laboratorios.recursion;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.indexedlist.*;
import es.upm.aedlib.positionlist.*;

public class Utils {

  public static int multiply(int a, int b) {
    int signo = a < 0 ? -1 : 1;
    return signo * auxMultiply(a, b);
  }

  private static int auxMultiply(int a, int b) {
    if (a == 0 || b == 0)
      return 0;
    int res = b;
    if (a % 2 != 0)
      res += auxMultiply(a / 2, res * 2);
    else
      res = auxMultiply(a / 2, res * 2);
    return res;
  }

  public static <E extends Comparable<E>> int findBottom(IndexedList<E> l) {
    if (l.size() == 0)
      return -1;
    return auxFindBottom(l, 0, l.size() - 1);
  }

  private static <E extends Comparable<E>> int auxFindBottom(IndexedList<E> l, int min, int max) {
    int length = max - min;
    if (length == 0)
      return min;
    else if (length > 0 && length < 2) {
      int comp = l.get(min).compareTo(l.get(max));
      if (comp <= 0)
        return min;
      else
        return max;
    } else if (length > 1) {
      int mid = min + (max - min) / 2;
      int compare = l.get(mid).compareTo(l.get(mid + 1));
      if (compare <= 0) {
        compare = l.get(mid).compareTo(l.get(mid - 1));
        if (compare <= 0)
          return mid;
        else
          return auxFindBottom(l, min, mid - 1);
      } else
        return auxFindBottom(l, mid + 1, max);
    }
    return -1;
  }

  public static <E extends Comparable<E>> NodePositionList<Pair<E, Integer>> joinMultiSets(
      NodePositionList<Pair<E, Integer>> l1, NodePositionList<Pair<E, Integer>> l2) {
        NodePositionList<Pair<E, Integer>> aux = new NodePositionList<>(l1);
    Position<Pair<E, Integer>> pointer1 = aux.first();
    Position<Pair<E, Integer>> pointer2 = l2.first();
    if ((pointer1 == null && pointer2 == null) || pointer2 == null)
      return new NodePositionList<>(l1);
    return auxJoinMultiSets(aux, l2, pointer1, pointer2);
  }

  private static <E extends Comparable<E>> NodePositionList<Pair<E, Integer>> auxJoinMultiSets(
      NodePositionList<Pair<E, Integer>> l1,
      NodePositionList<Pair<E, Integer>> l2, Position<Pair<E, Integer>> pointer1, Position<Pair<E, Integer>> pointer2) {
    if (pointer1 == null && pointer2 != null) {
      l1.addLast(pointer2.element());
      pointer2 = l2.next(pointer2);
      if (pointer2 != null)
        l1 = auxJoinMultiSets(l1, l2, pointer1, pointer2);
    } else if (pointer1.element().getLeft().compareTo(pointer2.element().getLeft()) < 0) {
        pointer1 = l1.next(pointer1);
      if (pointer2 != null)
        l1 = auxJoinMultiSets(l1, l2, pointer1, pointer2);
    } else {
      if (pointer1.element().getLeft().equals(pointer2.element().getLeft()))
        pointer1.element().setRight(pointer1.element().getRight() + pointer2.element().getRight());
      else {
        l1.addBefore(pointer1, pointer2.element());
        pointer1 = l1.prev(pointer1);
      }
      pointer2 = l2.next(pointer2);
      if (pointer2 != null)
        l1 = auxJoinMultiSets(l1, l2, pointer1, pointer2);
    }
    return l1;
  }
}
