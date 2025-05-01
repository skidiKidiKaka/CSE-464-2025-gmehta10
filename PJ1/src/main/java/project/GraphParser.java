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

    // Question3: Strategy/Template dispatch via enum
    public Path GraphSearch(Node src, Node dst, Algorithm algo) {
        switch (algo) {
            case BFS:
                return bfsTemplateSearch(src, dst);
            case DFS:
                return dfsTemplateSearch(src, dst);
            case RANDOMWALK:
                return randomWalkTemplateSearch(src, dst);
            default:
                return null;
        }
    }

    // Template-based BFS
    public Path bfsTemplateSearch(Node src, Node dst) {
        return new BFSTemplate(nodes, edges)
                .search(src.getLabel(), dst.getLabel());
    }

    // Template-based DFS
    public Path dfsTemplateSearch(Node src, Node dst) {
        return new DFSTemplate(nodes, edges)
                .search(src.getLabel(), dst.getLabel());
    }

    // Template-based Random Walk
    public Path randomWalkTemplateSearch(Node src, Node dst) {
        return new RandomWalkTemplate(nodes, edges)
                .search(src.getLabel(), dst.getLabel());
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
                        edges.add(new String[]{ src, dst });
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
        edges.add(new String[]{ srcLabel, dstLabel });
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
            ProcessBuilder pb = new ProcessBuilder(
                    "dot", "-T" + format, temp.getAbsolutePath(), "-o", filepath
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor();
            temp.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
