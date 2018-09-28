
import java.io.FileInputStream;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.*;

public class PuzzleSolver {

    public static final int GOAL_STATE[][] = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
    private State currentState = new State(new int[0][0]);
    private int maxNodes = 100000;

    // parses the given string into a state
    public void setState(String inputState) {
        Objects.requireNonNull(inputState);
        String state = inputState.replaceAll("\\s+", "");
        validateInputState(state);
        StringCharacterIterator stateIterator = new StringCharacterIterator(state);

        int[][] stateBuilder = new int[3][3];
        int currentChar = stateIterator.current();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state.charAt(i*3 + j) == 'b') {
                    stateBuilder[i][j] = 0;
                    currentState.setBlanki(i);
                    currentState.setBlankj(j);
                } else
                    stateBuilder[i][j] = stateIterator.current() - '0';
                stateIterator.next();
            }
        }
        currentState.setRepresentation(stateBuilder);
    }

    // sets maxNodes
    public void maxNodes(int maxNodes) {
        this.maxNodes = maxNodes;
    }

    // Changes the given state by swapping the blank with the tile adjacent in the given direction
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

    // Prints out the current state
    public void printState() {
        printState(currentState);
    }

    // Overrode to print any given state
    public void printState(State state) {
        System.out.println(Arrays.deepToString(state.getRepresentation()));
    }

    public void randomizeState(int numberOfMoves, State state) {
        Random random = new Random(59);
        while (numberOfMoves > 0) {
            makeRandomMove(state, random);
            numberOfMoves--;
        }
    }

    // The main A-Star solve method, takes h1 or h2 as an input heuristic type
    public void solveAStar(String heuristicType) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();

        Node currentNode = new Node(currentState, null, "none", 0);
        currentNode.setHeuristicCost(getHeuristic(heuristicType, currentState));
        priorityQueue.add(currentNode);

        int iterations = 0;
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

                 if (!Arrays.deepEquals(currentState.getRepresentation(),
                        node.getState().getRepresentation())) {
                    priorityQueue.add(node);
                }
            }

        }
        displayResults(currentNode, iterations);
    }

    // The main local beam solve method
    public void solveBeamSearch(int k) {
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();

        Node currentNode = new Node(currentState, null, "none", 0);
        currentNode.setHeuristicCost(getHeuristic("h2", currentState));
        priorityQueue.add(currentNode);

        int iterations = 0;
        while (!Arrays.deepEquals(currentState.getRepresentation(), GOAL_STATE)) {

            checkMaxNodes(iterations);

            Objects.requireNonNull(currentNode);
            Objects.requireNonNull(currentState);

//            System.out.println(priorityQueue.peek().getMoveDirection());
//            printState(priorityQueue.peek().getState());
//            System.out.println("H: " + priorityQueue.peek().getHeuristicCost());
//            System.out.println("T: " + priorityQueue.peek().getTotalPathCost());
//            System.out.println();

            currentNode = priorityQueue.poll();
            currentState = currentNode.getState();
            iterations++;

            Node up = exploreCurrentNode(currentNode, "up", "h2");
            Node down = exploreCurrentNode(currentNode, "down", "h2");
            Node left = exploreCurrentNode(currentNode, "left", "h2");
            Node right = exploreCurrentNode(currentNode, "right", "h2");

//            System.out.println("H: Up " + up.getHeuristicCost());
//            System.out.println("T: Up " + up.getTotalPathCost());
//            System.out.println("H: Down " + down.getHeuristicCost());
//            System.out.println("T: Down " + down.getTotalPathCost());
//            System.out.println("H: Left " + left.getHeuristicCost());
//            System.out.println("T: Left " + left.getTotalPathCost());
//            System.out.println("H: Right " + right.getHeuristicCost());
//            System.out.println("T: Right " + right.getTotalPathCost());
//            System.out.println();

            for (Node node : (new ArrayList<>(Arrays.asList(up, down, left, right)))) {
                if (!Arrays.deepEquals(currentNode.getState().getRepresentation(),
                        node.getState().getRepresentation())) {
                    priorityQueue.add(node);
                }
            }

            priorityQueue = reduceQueueToKBest(priorityQueue, k);
        }
        displayResults(currentNode, iterations);
    }


    //////////////////
    /*Helper Methods*/
    //////////////////
    private int getHeuristic(String heuristicType, State state) {
        if (heuristicType.equals("h1"))
            return getH1Heuristic(state);

        else if (heuristicType.equals("h2"))
            return getH2Heuristic(state);

        else throw new IllegalArgumentException("only h1 and h2 heuristic types are supported");
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
                movesAwaySum += (Math.abs(goali -i) + Math.abs(goalj - j));
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

    private State makeRandomMove(State state, Random random) {
        Double moveType = Math.ceil(random.nextDouble() * 4);

        if (moveType == 1)
            return move("up", state);
        else if (moveType == 2)
            return move("down", state);
        else if (moveType == 3)
            return move("left", state);
        else
            return move("right", state);
    }

    private void displayResults(Node currentNode, int iterations) {
        Node stackNode = currentNode;
        Stack<Node> stack = new Stack<>();
        ArrayList<String> movePrinter = new ArrayList<>();
        while (stackNode.getParentNode() != null) {
            stack.add(stackNode);
            stackNode = stackNode.getParentNode();
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

    private void checkMaxNodes(int iterations) {
        if (iterations > maxNodes)
            throw new UnsupportedOperationException("The number of nodes has exceeded " +
                    "the maximum allowable number of nodes");
    }


    // Helper for Move
    private State moveUp(State state) {
        if (state.getBlanki() != 0)
            return switchTiles(state, state.getBlanki(), state.getBlankj(),
                    state.getBlanki() - 1, state.getBlankj());
        else return state;
    }

    // Helper for Move
    private State moveDown(State state) {
        if (state.getBlanki() != 2)
            return switchTiles(state, state.getBlanki(), state.getBlankj(),
                    state.getBlanki() + 1, state.getBlankj());
        else return state;
    }

    // Helper for Move
    private State moveLeft(State state) {
        if (state.getBlankj() != 0)
            return switchTiles(state, state.getBlanki(), state.getBlankj(), state.getBlanki(),
                    state.getBlankj() - 1);
        else return state;
    }

    // Helper for Move
    private State moveRight(State state) {
        if (state.getBlankj() != 2)
            return switchTiles(state, state.getBlanki(), state.getBlankj(), state.getBlanki(),
                    state.getBlankj() + 1);
        else return state;
    }

    // Helper for Move, swaps tiles
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


    // Main Method
    public static void main(String[] args) {
        PuzzleSolver puzzleSolver = new PuzzleSolver();

        try {
            FileInputStream testFile = new FileInputStream(args[0]);

            Scanner fileScanner = new Scanner(testFile);

            while (fileScanner.hasNextLine()) {
                String currentLine = fileScanner.nextLine();
                System.out.println();
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


