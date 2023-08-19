package calculator;

import java.util.List;

class FakeRefiner implements Refiner {

    @Override
    public Numbers execute(final String text) {
        return new Numbers(List.of(new Number(1)));
    }
}
