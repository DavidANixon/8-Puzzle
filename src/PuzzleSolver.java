import javax.lang.model.type.ArrayType;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Objects;

public class PuzzleSolver {

    public static final int GOAL_STATE[][] = {{0, 2, 3}, {4, 5, 6}, {7, 8, 9}};
    private int currentState[][] = new int[3][3];
    private int currentBlanki = 0;
    private int currentBlankj = 0;

    public void setState(String inputState) {
        Objects.requireNonNull(inputState);
        String state = inputState.replaceAll("\\s+","");
        validateInputState(state);
        StringCharacterIterator stateIterator = new StringCharacterIterator(state);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state.charAt(i + j) == 'b') {
                    currentState[i][j] = 0;
                    currentBlanki = i;
                    currentBlankj = j;
                }
                else
                    currentState[i][j] = stateIterator.next() - '0';
            }
        }
    }

    public void move(String direction) {
        switch (direction) {
            case ("up"):
                moveUp();
                break;
            case ("down"):
                moveDown();
                break;
            case ("left"):
                moveLeft();
                break;
            case ("right"):
                moveRight();
                break;
            default: throw new IllegalArgumentException("the given direction does not match " +
                    "'up', 'down', 'left', or 'right");
        }
    }

    public void printState() {
        System.out.println("Current state of the puzzle is: \n" + Arrays.deepToString(currentState));
    }

    public void randomizeState(int numberOfMoves) {
        while (numberOfMoves > 0) {
            makeRandomMove();
            numberOfMoves--;
            printState();
        }
    }

    public void solveAStar(String heuristicType) {
        switch (heuristicType) {
            case ("H1Heuristic"):
                getH1Heuristic();
        }

    }

    //////////////////
    /*Helper Methods*/
    //////////////////
    private int getH1Heuristic() {
        int tilesOffGoal = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (currentState[i][j] != GOAL_STATE[i][j])
                    tilesOffGoal++;
            }
        }

        return tilesOffGoal;
    }

    private void makeRandomMove() {
        Double moveType = Math.ceil(Math.random() * 4);

        System.out.println(moveType);
        if (moveType == 1)
            move("up");
        else  if (moveType == 2)
            move("down");
        else if (moveType == 3)
            move("left");
        else
            move("right");
    }

    private void moveUp() {
        if (currentBlanki != 0)
            switchTiles(currentBlanki, currentBlankj, currentBlanki-1, currentBlankj);
    }

    private void moveDown() {
        if (currentBlanki != 2)
            switchTiles(currentBlanki, currentBlankj, currentBlanki+1, currentBlankj);
    }

    private void moveLeft() {
        if (currentBlankj != 0)
            switchTiles(currentBlanki, currentBlankj, currentBlanki, currentBlankj-1);
    }

    private void moveRight() {
        if (currentBlankj != 2)
            switchTiles(currentBlanki, currentBlankj, currentBlanki, currentBlankj+1);
    }

    private void switchTiles(int old_i, int old_j, int new_i, int new_j) {
        int tempHolder = currentState[old_i][old_j];
        currentState[old_i][old_j] = currentState[new_i][new_j];
        currentState[new_i][new_j] = tempHolder;
        currentBlanki = new_i;
        currentBlankj = new_j;
    }

    private void validateInputState(String state) {
        if (state.length() == 9
                && containsAll(state, "b", "1", "2", "3", "4", "5", "6", "7", "8")) {
            // no action is required
        } else
            throw new IllegalArgumentException("Given state does not match the required format");
    }

    private boolean containsAll(String state, String... toVerify) {
        for (CharSequence tile: toVerify) {
            if (!state.contains(tile))
                return false;
        }
        return true;
    }

    // Main Method

    public static void main(String[] args) {
        PuzzleSolver puzzleSolver = new PuzzleSolver();
        puzzleSolver.setState("b12 354 678");
        System.out.print(puzzleSolver.getH1Heuristic());
    }
}

