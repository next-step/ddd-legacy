package calculator;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 요구 사항
 *
 * 1. 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
 * 2. 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
 * 3. 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
public class StringCalculatorTest {

    @ParameterizedTest
    @MethodSource
    @DisplayName("쉼표(,)나 콜론(:)을 구분자로 갖는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환한다.")
    void addNumbersTest(String text, int sum) {
        assertThat(StringCalculator.add(text)).isEqualTo(sum);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> addNumbersTest() {
        return Stream.of(
            Arguments.of("", 0),
            Arguments.of("1,2", 3),
            Arguments.of("1,2,3", 6),
            Arguments.of("1,2:3", 6)
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("`//`와 `\\n`로 명시된 추가 구분자를 사용해도 일반적인 합 결과가 나타나야 한다.")
    void addNumbersTestWithCustomDelimiter(String text, int sum) {
        assertThat(StringCalculator.add(text)).isEqualTo(sum);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> addNumbersTestWithCustomDelimiter() {
        return Stream.of(
            Arguments.of("//;\\n1;2;3", 6),
            Arguments.of("//;\\n1,2:3;4", 10)
        );
    }
}
