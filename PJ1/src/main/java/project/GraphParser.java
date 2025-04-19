package project;

import java.io.*;
import java.util.*;

public class GraphParser {
    private Set<String> nodes;
    private List<String[]> edges;

    public GraphParser() {
        nodes = new HashSet<>();
        edges = new ArrayList<>();
    }

    // Question2: Template Pattern – delegate search to templates
    public Path GraphSearch(Node src, Node dst, Algorithm algo) {
        String start = src.getLabel();
        String target = dst.getLabel();
        switch (algo) {
            case BFS:
                return new BFSTemplate(nodes, edges).search(start, target);
            case DFS:
                return new DFSTemplate(nodes, edges).search(start, target);
            default:
                return null;
        }
    }

    // Refactor4: Rename Method – cleanLine (was normalizeLine)
    private String cleanLine(String rawLine) {
        String line = rawLine.trim();
        if (line.endsWith(";")) {
            line = line.substring(0, line.length() - 1).trim();
        }
        return line;
    }

    // Refactor3: Extract Method to consolidate file writing
    private void writeToFile(String filepath, String content) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.print(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseGraph(String filepath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String raw;
            boolean inGraph = false;
            while ((raw = br.readLine()) != null) {
                String line = cleanLine(raw);
                if (line.startsWith("digraph")) {
                    inGraph = true;
                    continue;
                }
                if (!inGraph || line.isEmpty() || line.equals("{") || line.equals("}")) {
                    continue;
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
        appendEdges(sb);
        Set<String> connected = new HashSet<>();
        for (String[] edge : edges) {
            connected.add(edge[0]);
            connected.add(edge[1]);
        }
        for (String node : nodes) {
            if (!connected.contains(node)) {
                sb.append("  ").append(node).append(";\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    // Refactor5: Extract Method – appendEdges
    private void appendEdges(StringBuilder sb) {
        for (String[] edge : edges) {
            sb.append("  ").append(edge[0]).append(" -> ").append(edge[1]).append(";\n");
        }
    }

    public void outputDOTGraph(String filepath) {
        writeToFile(filepath, toDOTString());
    }

    public void outputGraphics(String filepath, String format) {
        try {
            File temp = File.createTempFile("graph", ".dot");
            outputDOTGraph(temp.getAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder("dot", "-T" + format,
                    temp.getAbsolutePath(), "-o", filepath);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor();
            temp.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void outputGraph(String filepath) {
        writeToFile(filepath, toString());
    }

    public void removeNode(String label) {
        if (!nodes.contains(label)) {
            throw new IllegalArgumentException("Node does not exist: " + label);
        }
        nodes.remove(label);
        edges.removeIf(e -> e[0].equals(label) || e[1].equals(label));
    }

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

    public void removeEdge(String src, String dst) {
        boolean found = false;
        Iterator<String[]> it = edges.iterator();
        while (it.hasNext()) {
            String[] e = it.next();
            if (e[0].equals(src) && e[1].equals(dst)) {
                it.remove();
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Edge does not exist: " + src + " -> " + dst);
        }
    }

    // Refactor2: Extract Variable in BFS/DFS loops
    public Path GraphSearch(Node src, Node dst) {
        String start = src.getLabel();
        String target = dst.getLabel();
        if (!nodes.contains(start) || !nodes.contains(target)) {
            return null;
        }
        Map<String,String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> q = new LinkedList<>();
        visited.add(start);
        q.offer(start);

        while (!q.isEmpty()) {
            String cur = q.poll();
            if (cur.equals(target)) {
                return reconstructPath(start, target, prev);
            }
            for (String[] edge : edges) {
                String from = edge[0];
                String to   = edge[1];
                if (from.equals(cur) && !visited.contains(to)) {
                    visited.add(to);
                    prev.put(to, cur);
                    q.offer(to);
                }
            }
        }
        return null;
    }

    private Path reconstructPath(String start, String target, Map<String,String> prev) {
        List<String> path = new ArrayList<>();
        for (String at = target; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return (!path.isEmpty() && path.get(0).equals(start)) ? new Path(path) : null;
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
}
