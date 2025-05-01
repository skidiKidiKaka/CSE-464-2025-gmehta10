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

        // start and end of the node
        Node start = new Node("a");
        Node end   = new Node("h");

        // BFS
        System.out.println("=== BFS Path ===");
        Path bfsPath = parser.GraphSearch(start, end, Algorithm.BFS);
        if (bfsPath != null) {
            System.out.println(bfsPath);
        } else {
            System.out.println("No path found using BFS.");
        }

        // DFS
        System.out.println("\n=== DFS Path ===");
        Path dfsPath = parser.GraphSearch(start, end, Algorithm.DFS);
        if (dfsPath != null) {
            System.out.println(dfsPath);
        } else {
            System.out.println("No path found using DFS.");
        }

        // Random Walk Search
        System.out.println("\n=== Random Walk Search ===");
        for (int i = 1; i <= 10; i++) { // runs 10 times.
            System.out.println("Run " + i + ":");
            Path randomPath = parser.GraphSearch(start, end, Algorithm.RANDOMWALK);
            if (randomPath != null) {
                System.out.println(randomPath);
            } else {
                System.out.println("No path found on this run.");
            }
            System.out.println();
        }
    }
}
