package aed.Individuales.individual6;

import es.upm.aedlib.graph.Edge;
import es.upm.aedlib.graph.Vertex;

import java.util.Iterator;
import java.util.Optional;
import java.util.Stack;

import es.upm.aedlib.graph.DirectedGraph;
import es.upm.aedlib.map.Map;
import es.upm.aedlib.map.HashTableMap;

public class Suma {
  public static <E> Map<Vertex<Integer>, Integer> sumVertices(DirectedGraph<Integer, E> g) {
    Iterator<Vertex<Integer>> vertices = g.vertices().iterator();
    Map<Vertex<Integer>, Integer> result = new HashTableMap<>();
    while (vertices.hasNext()) {
      Vertex<Integer> elem = vertices.next();
      auxSumVertices(result, elem, elem, g, new Stack<>());
    }
    return result;
  }

  private static <E> void auxSumVertices(Map<Vertex<Integer>, Integer> map, Vertex<Integer> pivot,
      Vertex<Integer> current, DirectedGraph<Integer, E> graph, Stack<Vertex<Integer>> visited) {
    if (!visited.contains(current)) { //Si no ha sido visitado entonces sumar y comprobar caminos
      Iterator<Edge<E>> itr = graph.outgoingEdges(current).iterator(); //caminos desde current
      Optional<Integer> sum = Optional.ofNullable(map.get(pivot));

      visited.push(current);
      map.put(pivot, sum.orElse(0) + current.element()); //Sumar y guardar en el resultado

      if (itr != null) {
        while (itr.hasNext()) {
          Vertex<Integer> elem = graph.endVertex(itr.next()); //Siguiente elemento a comprobar como "current"
          auxSumVertices(map, pivot, elem, graph, visited);
        }
      }
    }
  }
}
