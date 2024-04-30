package racingcar;

public class SameNumberStrategy implements MoveStrategy{

    int distance;

    public SameNumberStrategy(int distance) {
        this.distance = distance;
    }

    @Override
    public int getDistance() {
        return distance;
    }
}
