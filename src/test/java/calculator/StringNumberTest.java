package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class StringNumberTest {
    private static final Stream<Arguments> throwWithNonNumericExceptionArguments() {
        return Stream.of(
                arguments(Arrays.asList(new String[]{"-1", "2", "3"})),
                arguments(Arrays.asList(new String[]{"1", "q", "3"}))
        );
    }

    @ParameterizedTest
    @MethodSource("throwWithNonNumericExceptionArguments")
    @DisplayName("숫자 이외의 값 또는 음수는 RuntimeException을 발생한다.")
    void throwWithNonNumericException(List<String> inputs) {
        assertThrows(IllegalArgumentException.class, () -> StringNumber.sum(inputs));
    }
}
