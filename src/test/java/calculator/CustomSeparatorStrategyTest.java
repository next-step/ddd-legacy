package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomSeparatorStrategyTest {

    private final CalculateStrategy calculateStrategy = new CustomSeparatorStrategy();

    @DisplayName("계산 값은 구분자를 기준으로 숫자간의 합을 반환한다")
    @CsvSource(value = {"//;\\n1;2;3-6", "//;\\n1;;2;;3-6", "//_\\n1_2_11-14"}, delimiter = '-')
    @ParameterizedTest
    void calculate(String input, int expected) {
        assertThat(calculateStrategy.calculate(input)).isEqualTo(expected);
    }

    @DisplayName("커스텀 구분자가 들어오지 않았을 경우 에러를 반환한다.")
    @CsvSource(value = {"1:2:3-6", "1::2::3-6", "1:2:11-14"}, delimiter = '-')
    @ParameterizedTest
    void noCustomSeparatorCalculate(String input, int expected) {
        assertThatThrownBy(() -> calculateStrategy.calculate(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("커스텀 구분자가 들어오지 않았습니다.");
    }

    @DisplayName("커스텀 구분자 대신 기본 구분자가 들어왓을 경우 에러를 반환한다.")
    @CsvSource(value = {"//;\\n1:2:3-6", "//;\\n1::2::3-6", "//_\\n1:2:11-14"}, delimiter = '-')
    @ParameterizedTest
    void useDefaultSeparatorCalculate(String input, int expected) {
        assertThatThrownBy(() -> calculateStrategy.calculate(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("숫자의 형태가 아닙니다.");
    }
    @DisplayName("커스텀 구분자를 지정할 경우 참을 반환한다.")
    @ValueSource(strings = {"//;\\n1;2:3", "//;\\n1;;2;;3", "//_\\n1_2_11"})
    @ParameterizedTest
    void canCalculate(String input) {
        assertThat(calculateStrategy.canCalculate(input)).isTrue();
    }

    @DisplayName("커스텀 구분자를 지정하지 않았을 경우 거짓을 반환한다.")
    @ValueSource(strings = {"1,1", "2:2"})
    @ParameterizedTest
    void cantCalculate(String input) {
        assertThat(calculateStrategy.canCalculate(input)).isFalse();

    }
}