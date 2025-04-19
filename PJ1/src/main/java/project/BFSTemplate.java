package project;

import java.util.*;


public class BFSTemplate
        extends GraphSearchTemplate
        implements GraphSearchStrategy {

    public BFSTemplate(Set<String> nodes, List<String[]> edges) {
        super(nodes, edges);
    }

    @Override
    protected Collection<String> createFrontier() {
        return new LinkedList<>();    // FIFO queue
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
        return ((Queue<String>) frontier).poll();
    }

    @Override
    protected void addToFrontier(String node) {
        ((Queue<String>) frontier).offer(node);
    }
}
