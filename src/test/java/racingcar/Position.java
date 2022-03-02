package racingcar;

public class Position {
    private int position;

    public Position(int position) {
        this.position = position;
    }

    public void nextPosition() {
        position += 1;
    }
}
