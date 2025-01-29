package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class NoSeparatorStrategyTest {

    private final CalculateStrategy calculateStrategy = new NoSeparatorStrategy();

    @DisplayName("계산 값은 0을 반환한다")
    @NullAndEmptySource
    @ParameterizedTest
    void calculate(String input) {
        assertThat(calculateStrategy.calculate(input)).isEqualTo(0);
    }

    @DisplayName("값이 비어있거나 Null일 시 참을 반환한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void canCalculate(String input) {
        assertThat(calculateStrategy.canCalculate(input)).isTrue();
    }

    @DisplayName("값이 비어있거나 Null이 아니라면 거짓을 반환한다.")
    @ValueSource(strings = {"1", "1:1", "1,2"})
    @ParameterizedTest
    void cantCalculate(String input) {
        assertThat(calculateStrategy.canCalculate(input)).isFalse();
    }
}