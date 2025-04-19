package project;

import java.util.*;


public class DFSTemplate
        extends GraphSearchTemplate
        implements GraphSearchStrategy {

    public DFSTemplate(Set<String> nodes, List<String[]> edges) {
        super(nodes, edges);
    }

    @Override
    protected Collection<String> createFrontier() {
        return new ArrayDeque<>();    // LIFO stack
    }

    @Override
    protected void initialize(String start) {
        visited.add(start);
        addToFrontier(start);
    }

    @Override
    protected boolean isFrontierEmpty() {
        return frontier.isEmpty();
    }

    @Override
    protected String getNext() {
        return ((Deque<String>) frontier).pop();
    }

    @Override
    protected void addToFrontier(String node) {
        ((Deque<String>) frontier).push(node);
    }
}
