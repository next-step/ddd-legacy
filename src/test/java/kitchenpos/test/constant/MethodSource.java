package kitchenpos.test.constant;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class MethodSource {

    public static final String NEGATIVE_NUMBERS = "kitchenpos.test.constant.MethodSource#negativeNumbers";

    private MethodSource() {
    }

    static Stream<Arguments> negativeNumbers() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-1)),
                Arguments.of(BigDecimal.valueOf(-100))
        );
    }
}
