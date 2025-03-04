package project;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java project.Main <dot-filepath>");
            return;
        }
        String filepath = args[0];
        GraphParser parser = new GraphParser();
        parser.parseGraph(filepath);
        System.out.println("Original Graph:");
        System.out.println(parser.toString());
        System.out.println("\nAdding nodes...");
        boolean addedX = parser.addNode("X");
        if (addedX) {
            System.out.println("Node 'X' added.");
        } else {
            System.out.println("Node 'X' already exists.");
        }
        parser.addNodes(new String[]{"Y", "Z", "a"});
        System.out.println("\nGraph after adding nodes:");
        System.out.println(parser.toString());
        System.out.println("\nAdding edges...");
        boolean addedEdge1 = parser.addEdge("X", "Y");
        if (addedEdge1) {
            System.out.println("Edge 'X -> Y' added.");
        } else {
            System.out.println("Edge 'X -> Y' already exists.");
        }
        boolean addedEdge2 = parser.addEdge("B", "C");
        if (addedEdge2) {
            System.out.println("Edge 'B -> C' added.");
        } else {
            System.out.println("Edge 'B -> C' already exists.");
        }
        boolean addedEdge3 = parser.addEdge("X", "Y");
        if (addedEdge3) {
            System.out.println("Edge 'X -> Y' added.");
        } else {
            System.out.println("Edge 'X -> Y' already exists.");
        }
        System.out.println("\nUpdated Graph:");
        System.out.println(parser.toString());
        System.out.println("\nOutputting DOT file to output.dot");
        parser.outputDOTGraph("output.dot");
        System.out.println("Outputting graphics to graph.png");
        parser.outputGraphics("graph.png", "png");
    }
}
