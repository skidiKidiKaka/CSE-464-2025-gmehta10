package project;

import java.util.*;

public class RandomWalkTemplate extends GraphSearchTemplate {
    private final Random rng = new Random();

    public RandomWalkTemplate(Set<String> nodes, List<String[]> edges) {
        super(nodes, edges);
    }

    @Override protected Collection<String> createFrontier() {
        return new ArrayList<>();  // random‐access removal
    }

    @Override protected void initialize(String start) {
        frontier   = createFrontier();
        visited    = new HashSet<>();
        parentMap  = new HashMap<>();
        frontier.add(start);
        visited.add(start);
    }

    @Override protected boolean isFrontierEmpty() {
        return frontier.isEmpty();
    }

    @Override protected String getNext() {
        // remove a random element
        List<String> list = (List<String>) frontier;
        int idx = rng.nextInt(list.size());
        return list.remove(idx);
    }

    @Override protected void addToFrontier(String node) {
        frontier.add(node);
    }
}
