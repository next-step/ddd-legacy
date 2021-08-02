package calculator;

import static calculator.Seperator.DEFAULT_DELIMITER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PositiveNumbersTest {

    @DisplayName("음수를 전달하는 경우 RuntimeException 예외 처리를 한다")
    @ParameterizedTest
    @ValueSource(strings = {"-1,2,3"})
    void negativeNumber(final String text) {
        assertThatThrownBy(
            () -> PositiveNumbers.of(text, DEFAULT_DELIMITER)
        ).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("숫자 이외의 값을 전달 하는 경우 RuntimeException 예외 처리를 한다")
    @ParameterizedTest
    @ValueSource(strings = {"1,a,3"})
    void notNumber(final String text) {
        assertThatThrownBy(
            () -> PositiveNumbers.of(text, DEFAULT_DELIMITER)
        ).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("0이상 더해서 int 최대값 이하의 값을 입력하는 경우 OK")
    @ParameterizedTest
    @ValueSource(strings = {"0,1,2", "150,12,30", "2,14,5,2,3,130", "100,4000,10000,20000", "1000,50000,100020,300000,400000"})
    void positiveNumber(final String text) {
        final PositiveNumbers numbers = PositiveNumbers.of(text, DEFAULT_DELIMITER);
        assertThat(numbers).isInstanceOf(PositiveNumbers.class);
    }

    @DisplayName("같은 값이면 equals도 같다")
    @ParameterizedTest
    @ValueSource(strings = {"0,1,2", "150,12,30", "2,14,5,2,3,130", "100,4000,10000,20000", "1000,50000,100020,300000,400000"})
    void equals(final String text) {
        final PositiveNumbers positiveNumbers1 = PositiveNumbers.of(text, DEFAULT_DELIMITER);
        final PositiveNumbers positiveNumbers2 = PositiveNumbers.of(text, DEFAULT_DELIMITER);
        assertThat(positiveNumbers1)
            .isEqualTo(positiveNumbers2);
    }

    @DisplayName("더할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"0,1,2", "150,12,30", "2,14,5,2,3,130", "100,4000,10000,20000", "1000,50000,100020,300000,400000"})
    void add(final String text) {
        final int actual = PositiveNumbers.of(text, DEFAULT_DELIMITER)
            .sum();
        final int expect = Arrays.stream(text.split(DEFAULT_DELIMITER))
            .mapToInt(Integer::parseInt)
            .sum();
        assertThat(actual).isEqualTo(expect);
    }

}