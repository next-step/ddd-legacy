package racingcar;

public class FakeRandomNumber implements RandomGenerator {

    private final int bound;

    public FakeRandomNumber(int bound) {
        this.bound = bound;
    }

    @Override
    public int generate(int bound) {
        return this.bound;
    }
}
