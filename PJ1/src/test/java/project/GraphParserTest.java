package project;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

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
    public void testRemoveNode() {
        GraphParser parser = new GraphParser();
        parser.addEdge("A", "B");
        parser.addEdge("B", "C");
        parser.addNode("D");
        // Remove node B; its incident edges (A->B and B->C) should be removed.
        parser.removeNode("B");
        String output = parser.toString();
        assertFalse(output.contains("B"));
        assertFalse(output.contains("A -> B"));
        assertFalse(output.contains("B -> C"));
        // Nodes A, C, and D should remain.
        assertTrue(output.contains("A"));
        assertTrue(output.contains("C"));
        assertTrue(output.contains("D"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentNode() {
        GraphParser parser = new GraphParser();
        parser.addNode("A");
        // Removing a node that doesn't exist should throw an exception.
        parser.removeNode("X");
    }

    @Test
    public void testRemoveNodes() {
        GraphParser parser = new GraphParser();
        parser.addEdge("A", "B");
        parser.addEdge("B", "C");
        parser.addEdge("C", "D");
        parser.addNode("E");
        // Remove nodes B and C.
        parser.removeNodes(new String[]{"B", "C"});
        String output = parser.toString();
        assertFalse(output.contains("B"));
        assertFalse(output.contains("C"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentNodes() {
        GraphParser parser = new GraphParser();
        parser.addNode("A");
        // Removing multiple nodes including one that doesn't exist.
        parser.removeNodes(new String[]{"A", "Z"});
    }

    @Test
    public void testRemoveEdge() {
        GraphParser parser = new GraphParser();
        parser.addEdge("A", "B");
        parser.addEdge("B", "C");
        // Remove edge A->B.
        parser.removeEdge("A", "B");
        String output = parser.toString();
        assertFalse(output.contains("A -> B"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentEdge() {
        GraphParser parser = new GraphParser();
        parser.addEdge("A", "B");
        // Removing a non-existent edge should throw an exception.
        parser.removeEdge("B", "A");
    }


    @Test
    public void testUnifiedGraphSearchDirectEdgeBFS() {
        GraphParser parser = new GraphParser();
        parser.addEdge("A", "B");
        // Test a direct edge search using BFS.
        Path path = parser.GraphSearch(new Node("A"), new Node("B"), Algorithm.BFS);
        assertNotNull("Expected a valid path from A to B using BFS", path);
        List<String> nodesInPath = path.getNodes();
        assertEquals(2, nodesInPath.size());
        assertEquals("A", nodesInPath.get(0));
        assertEquals("B", nodesInPath.get(1));
    }

    @Test
    public void testUnifiedGraphSearchDirectEdgeDFS() {
        GraphParser parser = new GraphParser();
        parser.addEdge("A", "B");
        // Test a direct edge search using DFS.
        Path path = parser.GraphSearch(new Node("A"), new Node("B"), Algorithm.DFS);
        assertNotNull("Expected a valid path from A to B using DFS", path);
        List<String> nodesInPath = path.getNodes();
        assertEquals(2, nodesInPath.size());
        assertEquals("A", nodesInPath.get(0));
        assertEquals("B", nodesInPath.get(1));
    }

    @Test
    public void testUnifiedGraphSearchMultipleStepsBFS() {
        GraphParser parser = new GraphParser();
        // Build a graph: A -> B, B -> C, A -> D, D -> C.
        parser.addEdge("A", "B");
        parser.addEdge("B", "C");
        parser.addEdge("A", "D");
        parser.addEdge("D", "C");
        // Expect the shortest path A -> B -> C when using BFS.
        Path path = parser.GraphSearch(new Node("A"), new Node("C"), Algorithm.BFS);
        assertNotNull("Expected a valid path from A to C using BFS", path);
        List<String> nodesInPath = path.getNodes();
        assertEquals("A", nodesInPath.get(0));
        assertEquals("C", nodesInPath.get(nodesInPath.size() - 1));
        assertEquals(3, nodesInPath.size());
    }

    @Test
    public void testUnifiedGraphSearchMultipleStepsDFS() {
        GraphParser parser = new GraphParser();
        // Build a graph: A -> B, B -> C, A -> D, D -> C.
        parser.addEdge("A", "B");
        parser.addEdge("B", "C");
        parser.addEdge("A", "D");
        parser.addEdge("D", "C");
        // DFS may choose a different intermediate node; expect a valid path from A to C.
        Path path = parser.GraphSearch(new Node("A"), new Node("C"), Algorithm.DFS);
        assertNotNull("Expected a valid path from A to C using DFS", path);
        List<String> nodesInPath = path.getNodes();
        assertEquals("A", nodesInPath.get(0));
        assertEquals("C", nodesInPath.get(nodesInPath.size() - 1));
        assertEquals(3, nodesInPath.size());
        String mid = nodesInPath.get(1);
        assertTrue(mid.equals("B") || mid.equals("D"));
    }

    @Test
    public void testUnifiedGraphSearchNoPathBFS() {
        GraphParser parser = new GraphParser();
        // Build a disconnected graph.
        parser.addEdge("A", "B");
        parser.addEdge("C", "D");
        // There is no path from A to D using BFS.
        Path path = parser.GraphSearch(new Node("A"), new Node("D"), Algorithm.BFS);
        assertNull("Expected no path from A to D using BFS", path);
    }

    @Test
    public void testUnifiedGraphSearchNoPathDFS() {
        GraphParser parser = new GraphParser();
        // Build a disconnected graph.
        parser.addEdge("A", "B");
        parser.addEdge("C", "D");
        // There is no path from A to D using DFS.
        Path path = parser.GraphSearch(new Node("A"), new Node("D"), Algorithm.DFS);
        assertNull("Expected no path from A to D using DFS", path);
    }

    @Test
    public void testUnifiedGraphSearchNonExistentNodesBFS() {
        GraphParser parser = new GraphParser();
        parser.addEdge("A", "B");
        // Searching with a non-existent node should return null using BFS.
        Path path = parser.GraphSearch(new Node("X"), new Node("B"), Algorithm.BFS);
        assertNull("Expected no path since node X does not exist using BFS", path);
    }

    @Test
    public void testUnifiedGraphSearchNonExistentNodesDFS() {
        GraphParser parser = new GraphParser();
        parser.addEdge("A", "B");
        // Searching with a non-existent node should return null using DFS.
        Path path = parser.GraphSearch(new Node("X"), new Node("B"), Algorithm.DFS);
        assertNull("Expected no path since node X does not exist using DFS", path);
    }
}
