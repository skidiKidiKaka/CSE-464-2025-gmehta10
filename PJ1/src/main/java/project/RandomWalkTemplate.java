package project;

import java.util.*;

public class RandomWalkTemplate extends GraphSearchTemplate {
    private final Random rng = new Random();

    public RandomWalkTemplate(Set<String> nodes, List<String[]> edges) {
        super(nodes, edges);
    }

    @Override
    protected Collection<String> createFrontier() {
        return new ArrayList<>();
    }

    @Override
    protected void initialize(String start) {
        frontier = createFrontier();
        visited = new HashSet<>();
        parentMap = new HashMap<>();
        addToFrontier(start);
        parentMap.put(start, null);
    }

    @Override
    protected boolean isFrontierEmpty() {
        return frontier.isEmpty();
    }

    @Override
    protected String getNext() {
        List<String> list = (List<String>) frontier;
        int idx = rng.nextInt(list.size());
        String node = list.remove(idx);
        System.out.println("random visiting Path{nodes=" + buildCurrentPathString(node) + "}");
        return node;
    }

    @Override
    protected void addToFrontier(String node) {
        frontier.add(node);
    }

    // helper to reconstruct partial path for logging
    private String buildCurrentPathString(String node) {
        List<String> p = new ArrayList<>();
        for (String at = node; at != null; at = parentMap.get(at)) {
            p.add(at);
        }
        Collections.reverse(p);
        return p.toString();
    }
}
