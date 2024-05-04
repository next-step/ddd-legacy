package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CalculatorTest {
    @Nested
    class AddTest {
        Calculator calculator;

        @BeforeEach
        void setUp() {
            calculator = new Calculator();
        }

        @DisplayName("null이나 빈값이 들어오면 0을 반환한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void isNullAndBlankTest(String input) {
            int result = calculator.add(input);

            assertThat(result).isZero();
        }

        @DisplayName("숫자 하나만 들어온다면 해당 숫자를 반환한다.")
        @Test
        void oneNumberTest() {
            int result = calculator.add("1");

            assertThat(result).isEqualTo(1);
        }

        @DisplayName("음수가 들어온 경우 RuntimeException을 던진다.")
        @Test
        void negativeNumberTest() {
            assertThatThrownBy(() -> calculator.add("-1"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("음수는 입력할 수 없습니다.");
        }

        @DisplayName("숫자가 아닌 값이 들어온 경우 RuntimeException을 던진다.")
        @Test
        void notNumberTest() {
            assertThatThrownBy(() -> calculator.add("a"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("숫자가 아닌 값이 포함되어 있습니다.");
        }

        @DisplayName("디폴트 구분자로 숫자를 더한다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,2,3", "1:2:3"})
        void defaultDelimiterTest(String input) {
            int result = calculator.add(input);

            assertThat(result).isEqualTo(6);
        }

        @DisplayName("커스텀 구분자로 숫자를 더한다.")
        @ParameterizedTest
        @ValueSource(strings = {"//;\n1;2;3", "//a\n1a2a3"})
        void customDelimiterTest(String input) {
            int result = calculator.add(input);

            assertThat(result).isEqualTo(6);
        }
    }
}
