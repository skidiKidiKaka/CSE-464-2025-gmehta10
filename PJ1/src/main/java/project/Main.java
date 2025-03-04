package project;


public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java project.Main <dot-filepath>");
            return;
        }

        String filepath = args[0];

        // Create parser and parse the DOT file
        GraphParser parser = new GraphParser();
        parser.parseGraph(filepath);

        // Print graph
        System.out.println(parser.toString());

    }
}
