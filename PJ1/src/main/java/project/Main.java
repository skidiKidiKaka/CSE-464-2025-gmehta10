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
        System.out.println("\nUpdated Graph:");
        System.out.println(parser.toString());
    }
}
