package project;

import java.util.List;
import java.util.Set;


public class GraphSearchStrategyFactory {
    public static GraphSearchStrategy getStrategy(
            Algorithm algo,
            Set<String> nodes,
            List<String[]> edges) {

        switch (algo) {
            case BFS:
                return new BFSTemplate(nodes, edges);
            case DFS:
                return new DFSTemplate(nodes, edges);
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algo);
        }
    }
}
