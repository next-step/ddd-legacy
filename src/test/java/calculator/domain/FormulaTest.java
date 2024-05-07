package calculator.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class FormulaTest {

    @Nested
    class constructorTest {
        @DisplayName("디폴트 구분자로 수식 생성이 가능하다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,2,3", "1:2:3"})
        void defaultDelimiterTest(String input) {
            assertThatCode(() -> new Formula(input)).doesNotThrowAnyException();
        }

        @DisplayName("커스텀 구분자로 수식 생성이 가능하다.")
        @ParameterizedTest
        @ValueSource(strings = {"//;\n1;2;3", "//a\n1a2a3"})
        void customDelimiterTest(String input) {
            assertThatCode(() -> new Formula(input)).doesNotThrowAnyException();
        }
    }

    @Nested
    class sumTest {

        @DisplayName("null이나 빈값이 들어오면 0을 리턴한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void blankTest(String input) {
            Formula formula = new Formula(input);
            assertThat(formula.getResult()).isZero();
        }

        @DisplayName("값이 하나만 들어오면 그 값의 갯수만 구할 수 있다.")
        @ParameterizedTest
        @ValueSource(strings = {"1", "2", "3"})
        void oneNumberTest(String input) {
            Formula formula = new Formula(input);
            assertThat(formula.getResult()).isEqualTo(Integer.parseInt(input));
        }

        @DisplayName("디폴트 구분자로 수식 생성한 합을 구할 수 있다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,2,3", "1:2:3"})
        void defaultDelimiterTest(String input) {
            Formula formula = new Formula(input);
            assertThat(formula.getResult()).isEqualTo(6);
        }

        @DisplayName("커스텀 구분자로 수식 생성한 합을 구할 수 있다.")
        @ParameterizedTest
        @ValueSource(strings = {"//;\n1;2;3", "//a\n1a2a3"})
        void customDelimiterTest(String input) {
            Formula formula = new Formula(input);
            assertThat(formula.getResult()).isEqualTo(6);
        }
    }
}
