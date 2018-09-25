import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Objects;

public class PuzzleSolver {

    public static final int GOAL_STATE[][] = {{'b', 2, 3}, {4, 5, 6}, {7, 8, 9}};
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
                moveleft();
                break;
            case ("right"):
                moveright();
                break;
            default: throw new IllegalArgumentException("the given direction does not match " +
                    "'up', 'down', 'left', or 'right");
        }
    }

    public void printState() {
        System.out.println("Current state of the puzzle is: \n" + Arrays.deepToString(currentState));
    }


    private void moveUp() {
        if (currentBlanki != 0)
            switchTiles(currentBlanki, currentBlankj, currentBlanki-1, currentBlankj);
    }
    private void moveDown() {
        if (currentBlanki != 2)
            switchTiles(currentBlanki, currentBlankj, currentBlanki+1, currentBlankj);
    }
    private void moveleft() {
        if (currentBlankj != 0)
            switchTiles(currentBlanki, currentBlankj, currentBlanki, currentBlankj-1);
    }
    private void moveright() {
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
                && state.contains("b")
                && state.contains("1")
                && state.contains("2")
                && state.contains("3")
                && state.contains("4")
                && state.contains("5")
                && state.contains("6")
                && state.contains("7")
                && state.contains("8")
        ) {
            // no action is required
        } else
            throw new IllegalArgumentException("Given state does not match the required format");
    }

    public static void main(String[] args) {
        PuzzleSolver puzzleSolver = new PuzzleSolver();
        puzzleSolver.setState("b12 345 678");
        puzzleSolver.printState();
        puzzleSolver.move("down");
        puzzleSolver.printState();
        puzzleSolver.move("down");
        puzzleSolver.printState();
        puzzleSolver.move("up");
        puzzleSolver.printState();
        puzzleSolver.move("left");
        puzzleSolver.printState();
        puzzleSolver.move("right");
        puzzleSolver.printState();
    }
}

