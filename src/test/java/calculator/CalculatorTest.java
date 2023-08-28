package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    public void init() {
        calculator = new Calculator(new NumberParsePolicyImpl(), new TokenizePolicyImpl());
    }

    @DisplayName("각 구분자에 의해 분리된 숫자를 더한 결과를 얻을수 있어야 한다.")
    @ParameterizedTest
    @MethodSource("test1MethodSource")
    void test1(String input, int expect) {
        //when
        int result = calculator.calc(input);

        //then
        assertThat(result).isEqualTo(expect);
    }

    static Stream<Arguments> test1MethodSource() {
        return Stream.of(
            Arguments.of("1,2,3", 6),
            Arguments.of("1:2:3", 6),
            Arguments.of("1,2:3", 6),
            Arguments.of("1:2,3", 6),
            Arguments.of("1:2,3", 6),
            Arguments.of("//;\n1;2;3", 6),
            Arguments.of("//!\n1!2!3", 6),
            Arguments.of("", 0),
            Arguments.of(" ", 0),
            Arguments.of("      ", 0),
            Arguments.of(null, 0)
        );
    }

    @DisplayName("구분자로 분리가 되지 못한 숫자가 있을시 에러가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2,,3", "1,:2,3", "//;\n1;2:3"})
    void test2(String input) {
        assertThatThrownBy(() -> calculator.calc(input))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("숫자 변환에 실패 하였습니다.");
    }

    @DisplayName("음수가 있을시 에러가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,-2,3", "-1:2:3", "//;\n1;2;-3", "-1"})
    void test3(String input) {
        assertThatThrownBy(() -> calculator.calc(input))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("음수는 사용할수 없습니다..");
    }
}