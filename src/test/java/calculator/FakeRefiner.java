package calculator;

import java.util.List;

class FakeRefiner implements Refiner {

    @Override
    public List<String> execute(final String text) {
        return List.of();
    }
}
