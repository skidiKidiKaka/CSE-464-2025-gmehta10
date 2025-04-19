package project;

import java.util.*;

public class DFSTemplate extends GraphSearchTemplate {
    public DFSTemplate(Set<String> nodes, List<String[]> edges) {
        super(nodes, edges);
    }

    @Override protected Collection<String> createFrontier() {
        return new ArrayDeque<>();
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
        return ((Deque<String>) frontier).pop();
    }

    @Override protected void addToFrontier(String node) {
        ((Deque<String>) frontier).push(node);
    }
}
