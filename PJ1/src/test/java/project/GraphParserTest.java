package project;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GraphParserTest {

    @Test
    public void testParseGraph() throws IOException {
        //  temporary DOT file
        File tempFile = File.createTempFile("testGraph", ".dot");
        tempFile.deleteOnExit();

        // Write digraph
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("digraph G {\n");
            writer.write("  A -> B;\n");
            writer.write("  B -> C;\n");
            writer.write("}\n");
        }

        // Instantiate and parse
        GraphParser parser = new GraphParser();
        parser.parseGraph(tempFile.getAbsolutePath());

        // Convert to string
        String output = parser.toString();
        System.out.println("Parsed Graph:\n" + output);

        // check number of nodes
        assertTrue(output.contains("Number of nodes: 3"));

        // check node labels
        assertTrue(output.contains("A"));
        assertTrue(output.contains("B"));
        assertTrue(output.contains("C"));

        // check number of edges
        assertTrue(output.contains("Number of edges: 2"));

        // check edges
        assertTrue(output.contains("A -> B"));
        assertTrue(output.contains("B -> C"));
    }
}
