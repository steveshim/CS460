package edu.csula.cs460.graph.search;

import edu.csula.cs460.graph.Edge;
import edu.csula.cs460.graph.Graph;
import edu.csula.cs460.graph.Node;
import edu.csula.cs460.graph.strategy.AdjacencyList;
import edu.csula.cs460.graph.strategy.Representation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AStar implements SearchStrategy {

    private Map<Node, IntPair> coordinate = new HashMap<>();
    private Map<IntPair, Node> ipCoordinate = new HashMap<>();
    private Graph graph = new Graph(Representation.of(Representation.STRATEGY.ADJACENCY_LIST));

    @Override
    public List<Edge> search(Graph graph, Node source, Node dist) {
        Map<Node, Node> parents = new HashMap<>();
        Map<Node, Integer> distances = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        List<Edge> result = new LinkedList<>();
        IntPair end = new IntPair(0, 0);

        PriorityQueue<Node> queue = new PriorityQueue<>(1, new Comparator<Node>() {
            public int compare(Node x, Node y){
                double distX = distances.get(x) + heuristic(x, dist);
                double distY = distances.get(y) + heuristic(y, dist);
                if (distX > distY)
                    return 1;
                else if (distY > distX)
                    return -1;
                else {
                    if(coordinate.get(x).getY()<coordinate.get(y).getY())
                        return -1;
                    if (coordinate.get(x).getX() >coordinate.get(y).getX() && Math.abs(coordinate.get(x).getY()-coordinate.get(y).getY()) == 1)
                        return 1;
                    else
                        return -1;
                }

            }
        });

        distances.put(source, 0);
        parents.put(source, null);
        queue.add(source);

        while (!queue.isEmpty()) {
            Node temp = queue.poll();

            if (temp.equals(dist)) {
                break;
            }

            for(Node node:graph.neighbors(temp)) {
                if (!visited.contains(node)) {
                    parents.put(node, temp);
                    distances.put(node, distances.get(temp) + 1);
                    visited.add(node);
                    queue.add(node);
                }
                else {
                    int newDistance = distances.get(temp) + 1;
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

    /**
     * A lower level implementation to get path from key point to key point
     */
    public String searchFromGridFile(File file) {
        String result = "";
        ArrayList<ArrayList<Integer>> maze = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        int xStart =0;
        int yStart = 0;
        int xEnd = 0;
        int yEnd = 0;

        Node source = null;
        Node dist = null;

        // TODO: read file and generate path using AStar algorithm

        try {
            Scanner sc = new Scanner(file);
            int row = 0;
            sc.nextLine(); //skip first line

            //build int array
            while (sc.hasNext()) {
                String tempLine = sc.nextLine();
                ArrayList<Integer> tempArray = new ArrayList<>();
                for (int i=1; i<tempLine.length(); i=i+2) {
                    if (tempLine.charAt(i) == '-' || tempLine.charAt(i) == '|') {
                        break;
                    }
                    else {
                        if (tempLine.charAt(i) == '#')
                            tempArray.add(-1);
                        else if (tempLine.charAt(i) == ' ')
                            tempArray.add(0);
                        else {
                            if (tempLine.charAt(i+1) == '1') {
                                xStart = i/2;
                                yStart = row;
                                tempArray.add(1);
                            }
                            else {
                                xEnd = i/2;
                                yEnd = row;
                                tempArray.add(2);
                            }
                        }
                    }
                }
                maze.add(tempArray);
                row++;
            }
            sc.close();



            int nodeCounter = 0;
            for (int i = 0; i < maze.size(); i++) {
                for (int j = 0; j < maze.get(i).size(); j++) {
                    if (maze.get(i).get(j).intValue() != -1) {
                        Node tempNode = new Node(nodeCounter);
                        IntPair tempIP = new IntPair(j, i);
                        graph.addNode(tempNode);
                        coordinate.put(tempNode, tempIP);
                        ipCoordinate.put(tempIP, tempNode);
                        if (maze.get(i).get(j).intValue() == 1) {
                            source = tempNode;
                        }
                        if (maze.get(i).get(j).intValue() == 2) {
                            dist = tempNode;
                        }
                        if (j != 0) {
                            if (maze.get(i).get(j -1).intValue() != -1) {
                                Edge fromCurrent = new Edge(tempNode, new Node(nodeCounter-1), 1);
                                Edge toCurrent = new Edge(new Node(nodeCounter-1), tempNode, 1);
                                graph.addEdge(fromCurrent);
                                graph.addEdge(toCurrent);
                            }
                        }
                        if (i != 0) {
                            if (maze.get(i - 1).get(j).intValue() != -1) {
                                Node upNode = ipCoordinate.get(new IntPair(j, i-1));
                                graph.addEdge(new Edge(tempNode, upNode, 1));
                                graph.addEdge(new Edge(upNode, tempNode, 1));
                            }
                        }
                        nodeCounter++;
                    }
                }
            }

            edges = search(graph, source, dist);

            for (int i = 0; i<edges.size(); i++) {
                Node from = edges.get(i).getFrom();
                Node to = edges.get(i).getTo();
                IntPair cFrom = coordinate.get(from);
                IntPair cTo = coordinate.get(to);
                if (cFrom.getX() < cTo.getX())
                    result = result + "E";
                else if (cFrom.getX() > cTo.getX())
                    result = result + "W";
                else if (cFrom.getY() < cTo.getY())
                    result = result + "S";
                else
                    result = result + "N";
            }


        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        return result;
    }

    public double heuristic(Node current, Node end){
        IntPair curIP = coordinate.get(current);
        IntPair endIP = coordinate.get(end);
        double doubleX = Math.pow((double)(curIP.getX() - endIP.getX()),2);
        double doubleY = Math.pow((double)(curIP.getY() - endIP.getY()),2);
        return Math.sqrt(doubleX + doubleY);
    }

}
