package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringCalculatorTest {

    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        assertThat(calculator.add(text)).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text) {
        assertThat(calculator.add(text)).isSameAs(Integer.parseInt(text));
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text) {
        assertThat(calculator.add(text)).isSameAs(3);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add("-1"));
    }


    @Nested
    @DisplayName("해석할 수 없는 형식")
    class outOfFormat {
        @DisplayName(value = "포맷에 맞지 않는 식을 입력한 경우 IllegalArgumentException 예외 처리를 한다.")
        @Test
        void outOfFormat() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> calculator.add("1abb3"));
        }

        @DisplayName(value = "구분자 외 다른 문자를 사용한 경우 IllegalArgumentException 예외 처리를 한다.")
        @Test
        void unreadableDelimiter() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> calculator.add("//+\n1-3"));
        }

        @DisplayName(value = "커스텀 구분자를 누락한 경우 IllegalArgumentException 예외 처리를 한다.")
        @Test
        void notFoundDelimiter() {
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> calculator.add("//\n1"));
        }
    }

    @Nested
    @DisplayName("구분자 관련 테스트")
    class delimiter {
        @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,2:3"})
        void colons(final String text) {
            assertThat(calculator.add(text)).isSameAs(6);
        }

        @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
        @ParameterizedTest
        @ValueSource(strings = {"//;\n1;2;3"})
        void customDelimiter(final String text) {
            assertThat(calculator.add(text)).isSameAs(6);
        }
    }

    @Nested
    @DisplayName("오버플로우 관련 테스트")
    class overflow {
        @DisplayName(value = "피연산자가 Integer 범위를 벗어나는 경우 NumberFormatException 예외 처리를 한다.")
        @Test
        void overflowOperand() {
            assertThatExceptionOfType(NumberFormatException.class)
                .isThrownBy(() -> calculator.add("9999999999"));
        }

        @DisplayName(value = "결과가 Integer의 범위를 벗어나는 경우 ArithmeticException 예외 처리를 한다.")
        @Test
        void overflowResults() {
            assertThatExceptionOfType(ArithmeticException.class)
                .isThrownBy(() -> calculator.add(Integer.MAX_VALUE + ",1"));
        }
    }
}
