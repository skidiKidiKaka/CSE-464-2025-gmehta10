package project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void outputGraph(String filepath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.print(this.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
