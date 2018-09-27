public class State {
    private int blanki;
    private int blankj;
    private int[][] representation;

    public int getBlanki() {
        return blanki;
    }

    public void setBlanki(int blanki) {
        this.blanki = blanki;
    }

    public int getBlankj() {
        return blankj;
    }

    public void setBlankj(int blankj) {
        this.blankj = blankj;
    }

    public int[][] getRepresentation() {
        return representation;
    }

    public void setRepresentation(int[][] representation) {
        this.representation = representation;
    }

    public State(int[][] representation) {
        this.representation = representation;
    }

}
