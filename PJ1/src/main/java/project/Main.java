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

        Node start = new Node("a");
        Node end   = new Node("h");

        System.out.println("--- search strategies (from 'a' to 'c') ---\n");

        System.out.println("performing bfs search:");
        Path bfsPath = parser.GraphSearch(start, end, Algorithm.BFS);
        System.out.println("bfs path: " + (bfsPath != null
                ? String.join(" -> ", bfsPath.getNodes())
                : "no path found") + "\n");

        System.out.println("performing dfs search:");
        Path dfsPath = parser.GraphSearch(start, end, Algorithm.DFS);
        System.out.println("dfs path: " + (dfsPath != null
                ? String.join(" -> ", dfsPath.getNodes())
                : "no path found") + "\n");

        System.out.println("performing random walk search (multiple runs):\n");
        for (int i = 1; i <= 5; i++) {
            System.out.println("run #" + i);
            Path rw = parser.GraphSearch(start, end, Algorithm.RANDOMWALK);
            System.out.println("random walk: " + (rw != null
                    ? String.join(" -> ", rw.getNodes())
                    : "no path found") + "\n");
        }
    }
}
