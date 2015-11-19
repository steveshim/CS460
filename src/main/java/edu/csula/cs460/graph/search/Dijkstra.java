package edu.csula.cs460.graph.search;

import edu.csula.cs460.graph.Edge;
import edu.csula.cs460.graph.Graph;
import edu.csula.cs460.graph.Node;

import java.util.*;

public class Dijkstra implements SearchStrategy {
    @Override
    public List<Edge> search(Graph graph, Node source, Node dist) {
        Map<Node, Node> parents = new HashMap<>();
        Map<Node, Integer> distances = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        List<Edge> result = new LinkedList<>();

        PriorityQueue<Node> queue = new PriorityQueue<>(1, new Comparator<Node>() {
            public int compare(Node x, Node y){
                Integer distX = distances.get(x);
                Integer distY = distances.get(y);
                if (distX > distY)
                    return 1;
                else if (distY > distX)
                    return -1;
                else
                    return 0;
            }
        });

        distances.put(source, 0);
        parents.put(source, null);
        queue.add(source);

        while (!queue.isEmpty()) {
            Node temp = queue.poll();

            for(Node node:graph.neighbors(temp)) {
                if (!visited.contains(node)) {
                    parents.put(node, temp);
                    distances.put(node, graph.distance(temp, node));
                    visited.add(node);
                    queue.add(node);
                }
                else {
                    Integer newDistance = distances.get(temp) + graph.distance(temp, node);
                    if (distances.get(node) > newDistance) {
                        distances.remove(node);
                        parents.remove(node);
                        distances.put(node, newDistance);
                        parents.put(node, temp);
                        queue.add(node);
                    }
                }
            }
        }


        //taken from BFS
        Node currentNode = dist;

        while (!currentNode.equals(source)) {
            Node parent = parents.get(currentNode);

            if (parent != null) {
                result.add(new Edge(parent, currentNode, graph.distance(parent, currentNode)));
            }

            currentNode = parents.get(currentNode);
        }

        Collections.reverse(result);

        return result;
    }
}

