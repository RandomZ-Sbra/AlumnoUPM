package aed.Laboratorios.delivery;

import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.graph.DirectedGraph;
import es.upm.aedlib.graph.DirectedAdjacencyListGraph;
import es.upm.aedlib.graph.Vertex;
import es.upm.aedlib.graph.Edge;
import es.upm.aedlib.map.HashTableMap;
import es.upm.aedlib.map.Map;
import es.upm.aedlib.set.HashTableMapSet;
import es.upm.aedlib.set.Set;
import java.util.Iterator;

public class Delivery<V> {
  private Map<Pair<Vertex<V>, Vertex<V>>, Integer> weigth;
  private DirectedAdjacencyListGraph<V, Integer> graph;

  /**
   * Construct a graph out of a series of vertices and an adjacency matrix. There
   * are 'len' vertices. A negative number means no connection. A non-negative
   * number represents distance between nodes.
   */
  public Delivery(V[] places, Integer[][] gmat) {
    Map<Integer, Vertex<V>> map = new HashTableMap<>();
    weigth = new HashTableMap<>();
    graph = new DirectedAdjacencyListGraph<>();
    map.put(0, graph.insertVertex(places[0]));
    for (int i = 0; i < gmat.length; i++) {
      for (int j = 0; j < gmat[i].length; j++) {
        if (!map.containsKey(j))
          map.put(j, graph.insertVertex(places[j]));
        if (j != i && gmat[i][j] != null && gmat[i][j] >= 0) {
          weigth.put(new Pair<>(map.get(i), map.get(j)),
              graph.insertDirectedEdge(map.get(i), map.get(j), gmat[i][j]).element());
        }
      }
    }
  }

  /** 
   * @return the graph that was constructed
   */
  public DirectedGraph<V, Integer> getGraph() {
    return graph;
  }

  /**
   * Return a Hamiltonian path for the stored graph, or null if there is none.
   * The list containts a series of vertices, with no repetitions (even if the
   * path can be expanded to a cycle).
   */
  public PositionList<Vertex<V>> tour() {
    PositionList<Vertex<V>> res = null;
    Iterator<Vertex<V>> nodes = graph.vertices().iterator();
    boolean done = false;
    while (nodes.hasNext() && !done) {
      Vertex<V> current = nodes.next();
      Iterator<Edge<Integer>> edges = graph.outgoingEdges(current).iterator();
      res = new NodePositionList<>();
      res.addFirst(current);
      Set<Vertex<V>> set = new HashTableMapSet<>();
      set.add(current);
      done = auxTour(res, current, edges, set);
    }
    if (done)
      return res;
    return null;
  }

  private boolean auxTour(PositionList<Vertex<V>> path, Vertex<V> current, Iterator<Edge<Integer>> edges,
      Set<Vertex<V>> visited) {
    boolean done = false;
    while (edges.hasNext() && !done) {
      Vertex<V> node = graph.endVertex(edges.next());
      if (weigth.containsKey(new Pair<>(current, node)) && !visited.add(node)) {
        path.addLast(node);
        done = auxTour(path, node, graph.outgoingEdges(node).iterator(), visited);
        if (!done) {
          visited.remove(path.remove(path.last()));
        }
      }
    }
    if (path.size() == graph.size())
      return true;
    return done;
  }

  public int length(PositionList<Vertex<V>> path) {
    Position<Vertex<V>> from = path.first();
    Position<Vertex<V>> to = path.next(from);
    if (to == null)
      return 0;
    int res = 0;
    while (to != null) {
      res += weigth.get(new Pair<>(from.element(), to.element()));
      from = to;
      to = path.next(to);
    }
    return res;
  }

  public String toString() {
    return "Delivery";
  }
}