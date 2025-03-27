package project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class GraphParser {
    private Set<String> nodes;
    private List<String[]> edges;

    public GraphParser() {
        nodes = new HashSet<>();
        edges = new ArrayList<>();
    }

    public void parseGraph(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean inGraphSection = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("digraph")) {
                    inGraphSection = true;
                    continue;
                }
                if (!inGraphSection) {
                    continue;
                }
                if (line.startsWith("{") || line.startsWith("}")) {
                    continue;
                }
                if (line.endsWith(";")) {
                    line = line.substring(0, line.length() - 1).trim();
                }
                if (line.contains("->")) {
                    String[] parts = line.split("->");
                    if (parts.length == 2) {
                        String src = parts[0].trim();
                        String dst = parts[1].trim();
                        nodes.add(src);
                        nodes.add(dst);
                        edges.add(new String[] { src, dst });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean addNode(String label) {
        return nodes.add(label);
    }

    public void addNodes(String[] labels) {
        for (String label : labels) {
            addNode(label);
        }
    }

    public boolean addEdge(String srcLabel, String dstLabel) {
        for (String[] edge : edges) {
            if (edge[0].equals(srcLabel) && edge[1].equals(dstLabel)) {
                return false;
            }
        }
        nodes.add(srcLabel);
        nodes.add(dstLabel);
        edges.add(new String[] { srcLabel, dstLabel });
        return true;
    }

    public String toDOTString() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");
        for (String[] edge : edges) {
            sb.append("  ").append(edge[0]).append(" -> ").append(edge[1]).append(";\n");
        }
        Set<String> connectedNodes = new HashSet<>();
        for (String[] edge : edges) {
            connectedNodes.add(edge[0]);
            connectedNodes.add(edge[1]);
        }
        for (String node : nodes) {
            if (!connectedNodes.contains(node)) {
                sb.append("  ").append(node).append(";\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    public void outputDOTGraph(String filepath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.print(toDOTString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputGraphics(String filepath, String format) {
        try {
            File tempDot = File.createTempFile("graph", ".dot");
            outputDOTGraph(tempDot.getAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder("dot", "-T" + format, tempDot.getAbsolutePath(), "-o", filepath);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();
            tempDot.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void outputGraph(String filepath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.print(this.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Remove a node and all its incident edges.
    public void removeNode(String label) {
        if (!nodes.contains(label)) {
            throw new IllegalArgumentException("Node does not exist: " + label);
        }
        nodes.remove(label);
        // Remove any edges incident to this node.
        edges.removeIf(edge -> edge[0].equals(label) || edge[1].equals(label));
    }

    // Remove multiple nodes. First, check that every node exists.
    public void removeNodes(String[] labels) {
        for (String label : labels) {
            if (!nodes.contains(label)) {
                throw new IllegalArgumentException("Node does not exist: " + label);
            }
        }
        for (String label : labels) {
            removeNode(label);
        }
    }

    // Remove an edge.
    public void removeEdge(String srcLabel, String dstLabel) {
        boolean removed = false;
        Iterator<String[]> iterator = edges.iterator();
        while (iterator.hasNext()) {
            String[] edge = iterator.next();
            if (edge[0].equals(srcLabel) && edge[1].equals(dstLabel)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        if (!removed) {
            throw new IllegalArgumentException("Edge does not exist: " + srcLabel + " -> " + dstLabel);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of nodes: ").append(nodes.size()).append("\n");
        sb.append("Nodes: ").append(nodes).append("\n");
        sb.append("Number of edges: ").append(edges.size()).append("\n");
        sb.append("Edges:\n");
        for (String[] edge : edges) {
            sb.append(edge[0]).append(" -> ").append(edge[1]).append("\n");
        }
        return sb.toString();
    }

    // New unified GraphSearch API with an enum parameter.
    // Depending on the value of 'algo' (BFS or DFS), it uses the corresponding search strategy.
    public Path GraphSearch(Node src, Node dst, Algorithm algo) {
        String start = src.getLabel();
        String target = dst.getLabel();
        if (!nodes.contains(start) || !nodes.contains(target)) {
            return null;
        }
        switch(algo) {
            case BFS:
                return bfsSearch(start, target);
            case DFS:
                return dfsSearch(start, target);
            default:
                return null;
        }
    }

    // Private helper method for BFS search.
    private Path bfsSearch(String start, String target) {
        Map<String, String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        visited.add(start);
        queue.offer(start);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(target)) {
                return reconstructPath(start, target, prev);
            }
            for (String[] edge : edges) {
                if (edge[0].equals(current)) {
                    String neighbor = edge[1];
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        prev.put(neighbor, current);
                        queue.offer(neighbor);
                    }
                }
            }
        }
        return null;
    }

    // Private helper method for DFS search.
    private Path dfsSearch(String start, String target) {
        Set<String> visited = new HashSet<>();
        Map<String, String> prev = new HashMap<>();
        boolean found = dfs(start, target, visited, prev);
        if (!found) {
            return null;
        }
        return reconstructPath(start, target, prev);
    }

    // Recursive DFS helper.
    private boolean dfs(String current, String target, Set<String> visited, Map<String, String> prev) {
        visited.add(current);
        if (current.equals(target)) {
            return true;
        }
        for (String[] edge : edges) {
            if (edge[0].equals(current)) {
                String neighbor = edge[1];
                if (!visited.contains(neighbor)) {
                    prev.put(neighbor, current);
                    if (dfs(neighbor, target, visited, prev)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Helper method to reconstruct the path from start to target using the predecessor map.
    private Path reconstructPath(String start, String target, Map<String, String> prev) {
        List<String> path = new ArrayList<>();
        for (String at = target; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        if (!path.isEmpty() && path.get(0).equals(start)) {
            return new Path(path);
        }
        return null;
    }
}
