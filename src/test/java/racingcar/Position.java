package racingcar;

public record Position(int position) {
    public Position up() {
        return new Position(position + 1);
    }
}
