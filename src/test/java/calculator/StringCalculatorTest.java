package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * # 요구 사항
 * <p>
 * - 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
 * - 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
 * - 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
public class StringCalculatorTest {
    private final StringCalculator calculator = new StringCalculator();

    @DisplayName("문자열이 비어있으면 0을 반환한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void nullOrEmpty(String text) {
        assertThat(calculator.add(text)).isZero();
    }

    @DisplayName("문자열에 숫자가 하나 있으면, 그 숫자를 반환한다.")
    @ParameterizedTest(name = "문자열이 \"{0}\"이면, 계산 결과는 {1}이다.")
    @CsvSource({
            "0, 0",
            "1, 1"
    })
    void singleNumber(String input, int output) {
        assertThat(calculator.add(input)).isEqualTo(output);
    }

    @DisplayName("콤마나 콜론으로 구분된 여러 숫자를 입력하면, 숫자의 합을 반환한다.")
    @ParameterizedTest(name = "\"{0}\"이면, 계산 결과는 {1}이다.")
    @CsvSource(value = {
            "1,2|3",
            "3:4|7",
            "5,6:7|18"
    }, delimiter = '|')
    void multipleNumberWithComma(String input, int output) {
        assertThat(calculator.add(input)).isEqualTo(output);
    }

    @DisplayName("//와 \\n 사이에 위치하는 문자를 커스텀 구분자로 사용")
    @ParameterizedTest(name = "\"{0}\"이면, 계산 결과는 {1}이다.")
    @CsvSource(value = {
            "'//;\n1;2'|3",
            "'//#\n'|0"
    }, delimiter = '|')
    void customizedDelimiter(String input, int output) {
        assertThat(calculator.add(input)).isEqualTo(output);
    }

    @DisplayName("올바르지 않은 문자열을 입력하면 예외를 발생시킨다.")
    @Test
    void illegalFormat() {
        assertThatIllegalArgumentException().isThrownBy(() -> calculator.add("\\\\;\n12"));
    }
}
