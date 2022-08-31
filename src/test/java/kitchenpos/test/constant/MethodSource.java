package kitchenpos.test.constant;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public final class MethodSource {

    public static final String NEGATIVE_NUMBERS = "kitchenpos.test.constant.MethodSource#negativeNumbers";
    public static final String EMPTY_LIST = "kitchenpos.test.constant.MethodSource#emptyList";

    private MethodSource() {
    }

    static Stream<Arguments> negativeNumbers() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-1)),
                Arguments.of(BigDecimal.valueOf(-100))
        );
    }

    static Stream<Arguments> emptyList() {
        return Stream.of(
                Arguments.of(Collections.emptyList())
        );
    }
}
