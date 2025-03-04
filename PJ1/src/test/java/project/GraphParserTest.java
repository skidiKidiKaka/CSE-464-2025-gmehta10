package project;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;

public class GraphParserTest {

    @Test
    public void testParseGraph() throws IOException {
        File tempFile = File.createTempFile("testGraph", ".dot");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("digraph G {\n");
            writer.write("  A -> B;\n");
            writer.write("  B -> C;\n");
            writer.write("}\n");
        }
        GraphParser parser = new GraphParser();
        parser.parseGraph(tempFile.getAbsolutePath());
        String output = parser.toString();
        System.out.println("Parsed Graph:\n" + output);
        assertTrue(output.contains("Number of nodes: 3"));
        assertTrue(output.contains("A"));
        assertTrue(output.contains("B"));
        assertTrue(output.contains("C"));
        assertTrue(output.contains("Number of edges: 2"));
        assertTrue(output.contains("A -> B"));
        assertTrue(output.contains("B -> C"));
    }

    @Test
    public void testAddNodeAndAddNodes() throws IOException {
        File tempFile = File.createTempFile("testGraph", ".dot");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("digraph G {\n");
            writer.write("  a -> b;\n");
            writer.write("  b -> c;\n");
            writer.write("}\n");
        }
        GraphParser parser = new GraphParser();
        parser.parseGraph(tempFile.getAbsolutePath());
        String output = parser.toString();
        assertTrue(output.contains("Number of nodes: 3"));
        boolean addedD = parser.addNode("d");
        assertTrue(addedD);
        boolean addedA = parser.addNode("a");
        assertFalse(addedA);
        parser.addNodes(new String[]{"e", "f", "a"});
        output = parser.toString();
        assertTrue(output.contains("Number of nodes: 6"));
        assertTrue(output.contains("d"));
        assertTrue(output.contains("e"));
        assertTrue(output.contains("f"));
    }

    @Test
    public void testAddEdge() throws IOException {
        File tempFile = File.createTempFile("testGraph", ".dot");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("digraph G {\n");
            writer.write("  A -> B;\n");
            writer.write("}\n");
        }
        GraphParser parser = new GraphParser();
        parser.parseGraph(tempFile.getAbsolutePath());
        boolean addedEdge = parser.addEdge("B", "C");
        assertTrue(addedEdge);
        boolean duplicateEdge = parser.addEdge("A", "B");
        assertFalse(duplicateEdge);
        boolean newEdge = parser.addEdge("X", "Y");
        assertTrue(newEdge);
    }

    @Test
    public void testOutputDOTGraph() throws IOException {
        GraphParser parser = new GraphParser();
        parser.addNode("A");
        parser.addNode("B");
        parser.addEdge("A", "B");
        parser.addNode("C");
        File tempDotFile = File.createTempFile("outputTest", ".dot");
        tempDotFile.deleteOnExit();
        parser.outputDOTGraph(tempDotFile.getAbsolutePath());
        String content = new String(Files.readAllBytes(tempDotFile.toPath()));
        assertTrue(content.contains("digraph"));
        assertTrue(content.contains("A -> B;"));
        assertTrue(content.contains("C;"));
    }

    @Test
    public void testOutputGraphics() throws IOException {
        GraphParser parser = new GraphParser();
        parser.addEdge("A", "B");
        File tempPng = File.createTempFile("graphOutput", ".png");
        tempPng.deleteOnExit();
        parser.outputGraphics(tempPng.getAbsolutePath(), "png");
        assertTrue(tempPng.exists());
        assertTrue(tempPng.length() > 0);
    }
}
