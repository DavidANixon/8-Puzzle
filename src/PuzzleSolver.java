
import java.io.FileInputStream;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.*;

public class PuzzleSolver {

    public static final int GOAL_STATE[][] = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
    private State currentState = new State(new int[0][0]);
    private int maxNodes = 1000000;

    public void setState(String inputState) {
        Objects.requireNonNull(inputState);
        String state = inputState.replaceAll("\\s+", "");
        validateInputState(state);
        StringCharacterIterator stateIterator = new StringCharacterIterator(state);

        int[][] stateBuilder = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state.charAt(i + j) == 'b') {
                    stateBuilder[i][j] = 0;
                    currentState.setBlanki(i);
                    currentState.setBlankj(j);
                } else
                    stateBuilder[i][j] = stateIterator.next() - '0';
            }
        }
        currentState.setRepresentation(stateBuilder);
    }

    public void maxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
    }

    public State move(String direction, State state) {
        switch (direction) {
            case ("up"):
                return moveUp(state);
            case ("down"):
                return moveDown(state);
            case ("left"):
                return moveLeft(state);
            case ("right"):
                return moveRight(state);
            case ("none"):
                return state;
            default:
                throw new IllegalArgumentException("the given direction does not match " +
                        "'up', 'down', 'left', or 'right");
        }
    }

    public void printState() {
        printState(currentState);
    }

    public void printState(State state) {
        System.out.println(Arrays.deepToString(state.getRepresentation()));
    }

    public void randomizeState(int numberOfMoves, State state) {
        while (numberOfMoves > 0) {
            makeRandomMove(state);
            numberOfMoves--;
        }
    }

    public void solveAStar(String heuristicType) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        Stack<Node> stack = new Stack<>();

        Node currentNode = new Node(currentState, null, "none", 0);
        currentNode.setHeuristicCost(getHeuristic(heuristicType, currentState));
        priorityQueue.add(currentNode);

        int iterations = 1;
        while (!Arrays.deepEquals(currentState.getRepresentation(), GOAL_STATE)) {

            checkMaxNodes(iterations);

            Objects.requireNonNull(currentNode);
            Objects.requireNonNull(currentState);
            currentNode = priorityQueue.poll();
            currentState = currentNode.getState();
            iterations++;

            Node up = exploreCurrentNode(currentNode, "up", heuristicType);
            Node down = exploreCurrentNode(currentNode, "down", heuristicType);
            Node left = exploreCurrentNode(currentNode, "left", heuristicType);
            Node right = exploreCurrentNode(currentNode, "right", heuristicType);


            for (Node node : (new ArrayList<>(Arrays.asList(up, down, left, right)))) {
                if (!Arrays.deepEquals(currentNode.getState().getRepresentation(),
                        node.getState().getRepresentation())) {
                    priorityQueue.add(node);
                }
            }

        }
        displayResults(currentNode, stack, iterations);
    }


    public void solveBeamSearch(int k) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
        Stack<Node> stack = new Stack<>();

        Node currentNode = new Node(currentState, null, "none", 0);
        currentNode.setHeuristicCost(getHeuristic("h1", currentState));
        priorityQueue.add(currentNode);

        int iterations = 1;
        while (!Arrays.deepEquals(currentState.getRepresentation(), GOAL_STATE)) {

            checkMaxNodes(iterations);

            Objects.requireNonNull(currentNode);
            Objects.requireNonNull(currentState);
            currentNode = priorityQueue.poll();
            currentState = currentNode.getState();
            iterations++;

            Node up = exploreCurrentNode(currentNode, "up", "h1");
            Node down = exploreCurrentNode(currentNode, "down", "h1");
            Node left = exploreCurrentNode(currentNode, "left", "h1");
            Node right = exploreCurrentNode(currentNode, "right", "h1");


            for (Node node : (new ArrayList<>(Arrays.asList(up, down, left, right)))) {
                if (!Arrays.deepEquals(currentNode.getState().getRepresentation(),
                        node.getState().getRepresentation())) {
                    priorityQueue.add(node);
                }
            }

            priorityQueue = reduceQueueToKBest(priorityQueue, k);
        }
        displayResults(currentNode, stack, iterations);
    }

    private void displayResults(Node currentNode, Stack<Node> stack, int iterations) {
        Node stackNode = currentNode;
        ArrayList<String> movePrinter = new ArrayList<>();
        while (stackNode.getPreviousNode() != null) {
            stack.add(stackNode);
            stackNode = stackNode.getPreviousNode();
        }

        while (!stack.isEmpty()) {
            stackNode = stack.pop();
            movePrinter.add(stackNode.getMoveDirection());
            System.out.println("Move " + stackNode.getMoveDirection());
            printState(stackNode.getState());
        }

        System.out.println("Number of moves to solution: " + currentNode.getTotalPathCost());
        System.out.println("Number of nodes considered: " + iterations);
        System.out.println(movePrinter.toString());
    }

    private PriorityQueue reduceQueueToKBest(PriorityQueue<Node> priorityQueue, int k) {
        if (priorityQueue.size() > k) {
            PriorityQueue<Node> reducedQueue = new PriorityQueue<>();
            for (int i = 0; i < k; i++) {
                reducedQueue.add(priorityQueue.poll());
            }
            priorityQueue.clear();

            for (int i = 0; i < k; i++) {
                priorityQueue.add(reducedQueue.poll());
            }
        }
        return priorityQueue;
    }

    private Node exploreCurrentNode(Node currentNode, String direction, String heuristicType) {
        State newState = move(direction, copyState(currentNode.getState()));
        Node newNode = new Node(newState, currentNode,
                direction, currentNode.getTotalPathCost() + 1);
        newNode.setHeuristicCost(getHeuristic(heuristicType, newState));
        return newNode;
    }

    //////////////////
    /*Helper Methods*/
    //////////////////
    private int getHeuristic(String heuristicType, State state) {
        if (heuristicType.equals("h1"))
            return getH1Heuristic(state);

        else if (heuristicType.equals("h2"))
            return getH2Heuristic(state);

        else throw new IllegalArgumentException("only H1 and H2 heuristic types are supported");
    }

    private int getH1Heuristic(State state) {
        int tilesOffGoal = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state.getRepresentation()[i][j] != GOAL_STATE[i][j])
                    tilesOffGoal++;
            }
        }

        return tilesOffGoal;
    }

    private int getH2Heuristic(State state) {
        int movesAwaySum = 0;
        int valueOfTile, goali, goalj;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                valueOfTile = state.getRepresentation()[i][j];
                goali = valueOfTile / 3;
                goalj = valueOfTile % 3;
                movesAwaySum += (Math.abs(goali - i + goalj - j));
            }
        }
        return movesAwaySum;
    }

    private State copyState(State state) {
        int[][] newRep = new int[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                newRep[i][j] = state.getRepresentation()[i][j];
            }
        }
        State newState = new State(newRep);
        newState.setBlanki(state.getBlanki());
        newState.setBlankj(state.getBlankj());
        return newState;
    }

    private State makeRandomMove(State state) {
        Double moveType = Math.ceil(Math.random() * 4);

        if (moveType == 1)
            return move("up", state);
        else if (moveType == 2)
            return move("down", state);
        else if (moveType == 3)
            return move("left", state);
        else
            return move("right", state);
    }

    private State moveUp(State state) {
        if (state.getBlanki() != 0)
            return switchTiles(state, state.getBlanki(), state.getBlankj(),
                    state.getBlanki() - 1, state.getBlankj());
        else return state;
    }

    private State moveDown(State state) {
        if (state.getBlanki() != 2)
            return switchTiles(state, state.getBlanki(), state.getBlankj(),
                    state.getBlanki() + 1, state.getBlankj());
        else return state;
    }

    private State moveLeft(State state) {
        if (state.getBlankj() != 0)
            return switchTiles(state, state.getBlanki(), state.getBlankj(), state.getBlanki(),
                    state.getBlankj() - 1);
        else return state;
    }

    private State moveRight(State state) {
        if (state.getBlankj() != 2)
            return switchTiles(state, state.getBlanki(), state.getBlankj(), state.getBlanki(),
                    state.getBlankj() + 1);
        else return state;
    }

    private State switchTiles(State state, int old_i, int old_j, int new_i, int new_j) {
        int tempHolder = state.getRepresentation()[old_i][old_j];
        state.getRepresentation()[old_i][old_j] = state.getRepresentation()[new_i][new_j];
        state.getRepresentation()[new_i][new_j] = tempHolder;
        state.setBlanki(new_i);
        state.setBlankj(new_j);
        return state;
    }

    private void validateInputState(String state) {
        if (state.length() == 9
                && containsAll(state, "b", "1", "2", "3", "4", "5", "6", "7", "8")) {
            // no action is required
        } else
            throw new IllegalArgumentException("Given state does not match the required format");
    }

    private boolean containsAll(String state, String... toVerify) {
        for (CharSequence tile : toVerify) {
            if (!state.contains(tile))
                return false;
        }
        return true;
    }

    private void checkMaxNodes(int iterations) {
        if (iterations > maxNodes)
            throw new UnsupportedOperationException("The number of nodes has exceeded " +
                    "the maximum allowable number of nodes");
    }

    // Main Method
    public static void main(String[] args) {
        PuzzleSolver puzzleSolver = new PuzzleSolver();

        try {
            FileInputStream testFile = new FileInputStream("test.txt");

            Scanner fileScanner = new Scanner(testFile);

            while (fileScanner.hasNextLine()) {
                String currentLine = fileScanner.nextLine();
                System.out.println(currentLine);
                String[] commands = currentLine.split(" ");

                switch (commands[0]) {
                    case ("setState"):
                        puzzleSolver.setState(commands[1] + " " + commands[2] + " " + commands[3]);
                        puzzleSolver.printState();
                        break;
                    case ("randomizeState"):
                        puzzleSolver.randomizeState(Integer.parseInt(commands[1]), puzzleSolver.currentState);
                        puzzleSolver.printState();
                        break;
                    case ("printState"):
                        puzzleSolver.printState();
                        break;
                    case ("move"):
                        puzzleSolver.move(commands[1], puzzleSolver.currentState);
                        puzzleSolver.printState();
                        break;
                    case ("solve"):
                        if (commands[1].equals("A-star"))
                            puzzleSolver.solveAStar(commands[2]);
                        else if (commands[1].equals("beam")) {
                            puzzleSolver.solveBeamSearch(Integer.parseInt(commands[2]));
                        }
                        break;
                    case ("maxNodes"):
                        puzzleSolver.maxNodes(Integer.parseInt(commands[1]));
                        break;
                }
            }

        } catch (IOException ioe) {
            System.out.println("Could not read file test.txt");
        }
    }
}


