package calculator.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class FormulaTest {

    @Nested
    class ofTest {
        @DisplayName("디폴트 구분자로 수식 생성이 가능하다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,2,3", "1:2:3"})
        void defaultDelimiterTest(String input) {
            assertThatCode(() -> Formula.of(input)).doesNotThrowAnyException();
        }

        @DisplayName("커스텀 구분자로 수식 생성이 가능하다.")
        @ParameterizedTest
        @ValueSource(strings = {"//;\n1;2;3", "//a\n1a2a3"})
        void customDelimiterTest(String input) {
            assertThatCode(() -> Formula.of(input)).doesNotThrowAnyException();
        }
    }

    @Nested
    class sumTest {
        @DisplayName("디폴트 구분자로 수식 생성한 합을 구할 수 있다.")
        @ParameterizedTest
        @ValueSource(strings = {"1,2,3", "1:2:3"})
        void defaultDelimiterTest(String input) {
            Formula formula = Formula.of(input);
            assertThat(formula.sum()).isEqualTo(6);
        }

        @DisplayName("커스텀 구분자로 수식 생성한 합을 구할 수 있다.")
        @ParameterizedTest
        @ValueSource(strings = {"//;\n1;2;3", "//a\n1a2a3"})
        void customDelimiterTest(String input) {
            Formula formula = Formula.of(input);
            assertThat(formula.sum()).isEqualTo(6);
        }
    }
}
