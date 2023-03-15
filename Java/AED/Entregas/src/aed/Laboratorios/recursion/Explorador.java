package aed.Laboratorios.recursion;

import java.util.Iterator;
import java.util.Stack;

import es.upm.aedlib.Pair;
import es.upm.aedlib.positionlist.*;

public class Explorador {
  private static boolean found;
  private static Lugar goal;
  private static PositionList<Lugar> path;

  public static Pair<Object, PositionList<Lugar>> explora(Lugar inicialLugar) {
    found = false;
    goal = null;
    path = new NodePositionList<>();
    inicialLugar.marcaSueloConTiza();
    path.addLast(inicialLugar);
    Stack<PositionList<Lugar>> noFijados = new Stack<>();
    profAlgorithm(noFijados, path, inicialLugar);
    if(goal != null)
    return new Pair<>(goal.getTesoro(), Explorador.path);
    else
    return null;
  }

  private static boolean profAlgorithm(Stack<PositionList<Lugar>> pGuardados, PositionList<Lugar> fijados,
      Lugar origen) {
    if (fijados.last() != null && !fijados.last().element().equals(origen)) {
      fijados.addLast(origen);
      fijados.last().element().marcaSueloConTiza();
    }
    origen.printLaberinto();

    PositionList<Lugar> routes = new NodePositionList<Lugar>();
    Iterator<Lugar> itr = fijados.last().element().caminos().iterator();
    int probables = 0;
    while (itr.hasNext() && !found) {
      Lugar l = itr.next();
      if (l != null && !l.sueloMarcadoConTiza()) {
        probables++;
        routes.addLast(l);
        if (l.tieneTesoro()) {
          fijados.addLast(l);
          found = true;
          goal = l;
          Explorador.path = fijados;
          probables = 0;
        }
      }
    }
    if (found) {
      return true;
    }
    if (probables == 0 && pGuardados.isEmpty()) {
      return false;
    }
    if (probables == 0 && pGuardados.peek().last().element().equals(origen)) {
      pGuardados.pop();
      fijados = pGuardados.peek();
      return profAlgorithm(pGuardados, fijados, pGuardados.peek().last().element());
    } else if (probables == 0) {
      fijados = pGuardados.peek();
      return profAlgorithm(pGuardados, fijados, pGuardados.peek().last().element());
    } else if (probables == 1) {
      return profAlgorithm(pGuardados, fijados, routes.last().element());
    } else {
      PositionList<Lugar> checkPoint = new NodePositionList<>(fijados);
      pGuardados.push(checkPoint);
      return profAlgorithm(pGuardados, fijados, routes.first().element());
    }
  }
}
