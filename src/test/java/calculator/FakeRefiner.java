package calculator;

import java.util.List;

class FakeRefiner implements Refiner {

    @Override
    public PositiveNumbers execute(final String text) {
        return new PositiveNumbers(List.of(new PositiveNumber(1)));
    }
}
