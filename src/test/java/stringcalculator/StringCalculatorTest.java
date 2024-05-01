package stringcalculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class StringCalculatorTest {
    @ParameterizedTest
    @MethodSource("successTestFixture")
    @DisplayName("쉼표 또는 콜론을 구분자로 이루어진 숫자를 입력하면, 숫자들의 총합을 반환한다.")
    void successTest(String userInput, int expected) {
        // when
        var result = new StringCalculator(userInput).calculate();

        // then
        Assertions.assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> successTestFixture() {
        return Stream.of(
                Arguments.of("1,2", 3),
                Arguments.of("1;2;3", 6),
                Arguments.of("", 0)
        );
    }

    @DisplayName("//와 \\n 사이에 위치한 문자를 문자열에 포함시, 이를 커스텀 구분자로 사용, 숫자들의 총합을 반환한다.")
    void customDelimiterTest() {

    }

    @DisplayName("문자열 계산기에 숫자 이외의 값 혹은 음수를 입력시 RuntimeException 발생한다.")
    void invalidInputExceptionTest() {

    }
}