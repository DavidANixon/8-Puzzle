public class Node implements Comparable<Node>{
    private Node previousNode;
    private String moveDirection;
    private int totalPathCost;
    private int heuristicCost;
    private State state;

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
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

    public Node (State state, Node previousNode, String moveDirection, int totalPathCost) {
        this.state = state;
        this.previousNode = previousNode;
        this.moveDirection = moveDirection;
        this.totalPathCost = totalPathCost;
    }

    @Override
    public int compareTo(Node compNode) {
        if (totalPathCost + heuristicCost > compNode.totalPathCost + compNode.heuristicCost)
            return 1;
        else
            return 0;
    }
}
