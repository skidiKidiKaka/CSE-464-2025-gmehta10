package project;

import java.util.*;

public abstract class GraphSearchTemplate {
    protected Set<String> nodes;
    protected List<String[]> edges;

    protected Set<String> visited;
    protected Map<String,String> parentMap;
    protected Collection<String> frontier;

    protected GraphSearchTemplate(Set<String> nodes, List<String[]> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public Path search(String start, String target) {
        if (!nodes.contains(start) || !nodes.contains(target)) return null;

        visited   = new HashSet<>();
        parentMap = new HashMap<>();
        frontier  = createFrontier();

        initialize(start);

        while (!isFrontierEmpty()) {
            String current = getNext();
            if (current.equals(target)) {
                return buildPath(start, target);
            }
            for (String[] edge : edges) {
                if (edge[0].equals(current)) {
                    String neighbour = edge[1];
                    if (!visited.contains(neighbour)) {
                        visited.add(neighbour);
                        parentMap.put(neighbour, current);
                        addToFrontier(neighbour);
                    }
                }
            }
        }
        return null;
    }

    private Path buildPath(String start, String target) {
        List<String> path = new ArrayList<>();
        for (String at = target; at != null; at = parentMap.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path.get(0).equals(start) ? new Path(path) : null;
    }

    protected abstract Collection<String> createFrontier();
    protected abstract void initialize(String start);
    protected abstract boolean isFrontierEmpty();
    protected abstract String getNext();
    protected abstract void addToFrontier(String node);
}
