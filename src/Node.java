public class Node implements Comparable<Node>{
    private Node parentNode;
    private String moveDirection;
    private int totalPathCost;
    private int heuristicCost;
    private State state;

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node previousNode) {
        this.parentNode = previousNode;
    }

    public String getMoveDirection() {
        return moveDirection;
    }

    public void setMoveDirection(String moveDirection) {
        this.moveDirection = moveDirection;
    }

    public int getTotalPathCost() {
        return totalPathCost;
    }

    public void setTotalPathCost(int totalPathCost) {
        this.totalPathCost = totalPathCost;
    }

    public int getHeuristicCost() {
        return heuristicCost;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setHeuristicCost(int heuristicCost) {
        this.heuristicCost = heuristicCost;
    }

    public Node (State state, Node parentNode, String moveDirection, int totalPathCost) {
        this.state = state;
        this.parentNode = parentNode;
        this.moveDirection = moveDirection;
        this.totalPathCost = totalPathCost;
    }

    // Tells the priority queue what to base the ranking on
    @Override
    public int compareTo(Node compNode) {
        if ((totalPathCost + heuristicCost) > (compNode.totalPathCost + compNode.heuristicCost))
            return 1;
        else
            return -1;
    }
}
