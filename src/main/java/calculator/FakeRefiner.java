package calculator;

import com.google.common.collect.ImmutableList;
import java.util.List;

class FakeRefiner implements Refiner {

    @Override
    public List<String> execute(final String text) {
        return ImmutableList.of();
    }
}
