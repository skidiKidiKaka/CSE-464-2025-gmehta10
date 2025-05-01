package project;

import java.util.*;

public class BFSTemplate extends GraphSearchTemplate {
    public BFSTemplate(Set<String> nodes, List<String[]> edges) {
        super(nodes, edges);
    }

    @Override
    protected Collection<String> createFrontier() {
        return new LinkedList<>();
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
        String node = ((Queue<String>) frontier).poll();
        System.out.println("bfs visiting: " + node);
        return node;
    }

    @Override
    protected void addToFrontier(String node) {
        ((Queue<String>) frontier).offer(node);
    }
}
