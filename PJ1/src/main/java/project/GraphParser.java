package project;

import java.io.BufferedReader;
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

                // looks for begining of digraph
                if (line.startsWith("digraph")) {
                    inGraphSection = true;
                    continue;
                }
                // skip till digraph
                if (!inGraphSection) {
                    continue;
                }

                // Skip brackets
                if (line.startsWith("{") || line.startsWith("}")) {
                    continue;
                }

                // Removes ending semicolon if present
                if (line.endsWith(";")) {
                    line = line.substring(0, line.length() - 1).trim();
                }

                // Look for -> for edges
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
