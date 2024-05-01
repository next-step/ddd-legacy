package racingcar;

public record Position(int pos) {
    public Position up() {
        return new Position(pos + 1);
    }
}
