package aed.Individuales.individual2;

import es.upm.aedlib.indexedlist.*;

public class IndexedListCheckSumUtils {

  // a no es null, podria tener tamaño 0, n>0
  public static IndexedList<Integer> indexedListCheckSum(IndexedList<Integer> list, int n) {
    int sum = 0;
    int currPos = 0;
    int sumPos = n;
    ArrayIndexedList<Integer> r = new ArrayIndexedList<>((Integer[])list.toArray(new Integer[list.size()]));
    while (currPos < r.size()) { //Bucle para recorrer toda las posiciones y almacenar las sumas en "sum".
      if (currPos != sumPos && r.get(currPos) != null) { //Si posición actual no es "posición suma" entonces:
        sum += r.get(currPos); //almacenar suma
        currPos++; //pasar a siguiente posición
      } else if (currPos == sumPos) { //Si mi posición es "posición suma" entonces:
        r.add(currPos, sum); //añadir suma almacenada en la posición
        sum = 0; //resetear suma
        sumPos += n+1; //actualizar posición suma
        currPos++; //pasar a siguiente posición
      } else { //Si ocurre otro caso (como encontrar un elemento nulo)
        currPos++; //pasar a siguiente posición ignorando la actual
      }
    }
    if (sumPos != r.size()-1 && list.size() > 0) { //Si la "posición suma" residual es distinta que la longitud de la lista resultado y la lista original tiene un tamaño superior a 0 entonces:
      r.add(r.size(), sum); //añadir la suma de los últimos elementos residuales de la lista
    }
    return r;
  }

  // list no es null, podria tener tamaño 0, n>0
  public static boolean checkIndexedListCheckSum(IndexedList<Integer> list, int n) {
    int sum = 0;
    int currPos = 0;
    int sumPos = n;
    boolean r = true;
    while (currPos < list.size()-1 && r) {
      if (currPos != sumPos && list.get(currPos) != null) {
        sum += list.get(currPos);
        currPos++;
      } else if (currPos == sumPos) {
        r = sum == list.get(currPos);
        sum = 0;
        sumPos += n+1;
        currPos++;
      } else {
        currPos++;
      }
    }
    if (list.size() > 0) {
      r = list.get(list.size()-1) == sum;
    }
    return r;
  }

  public static void main(String[] args) {
    IndexedList<Integer> l = new ArrayIndexedList<>();
    l.add(0, 2);
    l.add(1, 4);
    l.add(2, 7);
    l.add(3, 5);
    l.add(4, 3);
    System.out.println(checkIndexedListCheckSum(indexedListCheckSum(l, 1), 1));
  }
}

