package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class BaseSeparatorStrategyTest {

    private final CalculateStrategy calculateStrategy = new BaseSeparatorStrategy();

    @DisplayName("숫자 두개를 컴마 구분자의 합을 반환한다.")
    @CsvSource(value = {"1,1-2", "2,2-4", "3,33-36"}, delimiter = '-')
    @ParameterizedTest
    void separateCommaCheck(String input, int expected) {
        assertThat(calculateStrategy.calculate(input)).isEqualTo(expected);
    }

    @DisplayName("구분자를 컴마(,) 이외에 콜론(:)을 사용할 수 있다.")
    @CsvSource(value = {"1,,1-2", "1,2:3-6", "2:123-125", "3,11-14"}, delimiter = '-')
    @ParameterizedTest
    void pluralSeparateCheck(String input, int expected) {
        assertThat(calculateStrategy.calculate(input)).isEqualTo(expected);
    }

    @DisplayName("커스텀 구분자를 사용하지 않고 빈 값도 아닐시 무조건 참을 반환한다.")
    @ValueSource(strings = {"1:1, 5, 1:1:1, 1,1,1,1"})
    @ParameterizedTest
    void canCalculate(String input) {
        assertThat(calculateStrategy.canCalculate(input)).isTrue();
    }
}