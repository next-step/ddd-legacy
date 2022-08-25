package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringCalculatorTest {

    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName("1. 빈 문자열 또는 null을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void isBlank(String text) {
        assertThat(calculator.add(text)).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void onlyOneNumber(String actual) {
        assertThat(calculator.add(actual))
                .isEqualTo(1);
    }

    @DisplayName("'//'와 '\n' 문자 사이에 커스텀 구분자를 지정할 수 있다. ")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(String actual) {
        assertThat(calculator.add(actual))
                .isEqualTo(6);
    }

    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 "
            + "구분자를 기준으로 분리한 각 숫자의 합을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"=0", "1,2=3", "1,2,3=6", "1,2:3=6"}, delimiter = '=')
    void add(String actual, int expected) {
        assertThat(calculator.add(actual))
                .isEqualTo(expected);
    }
}
