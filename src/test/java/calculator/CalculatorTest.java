package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.transaction.NotSupportedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void 빈문자열_널_테스트(String text) throws NotSupportedException {
        assertThat(calculator.add(text)).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "5"})
    void 한개숫자테스트(final String text) throws NotSupportedException {
        assertThat(calculator.add(text)).isSameAs(Integer.parseInt(text));
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,2=3", "2,3=5", "10,13=23"}, delimiter = '=')
    void 두개숫자테스트(final String input, final Integer result) throws NotSupportedException {
        assertThat(calculator.add(input)).isEqualTo(result);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"1,2:3=6", "2,3:5=10", "10,13:2=25"}, delimiter = '=')
    void 구분자테스트(final String input, final Integer result) throws NotSupportedException {
        assertThat(calculator.add(input)).isEqualTo(result);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void 커스텀구분자테스트(final String input) throws NotSupportedException {
        assertThat(calculator.add(input)).isEqualTo(6);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "1,-2", "1,2:-3", "//;\n1;-2;3"})
    void 음수테스트(final String input) {
        assertThatThrownBy(() -> calculator.add(input))
                .isInstanceOf(RuntimeException.class);
    }

}
