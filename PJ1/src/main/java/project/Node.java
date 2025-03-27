package project;

public class Node {
    private String label;

    public Node(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Node))
            return false;
        Node other = (Node) obj;
        return label.equals(other.label);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }
}
