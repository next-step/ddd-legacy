package stringcalculator.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import stringcalculator.operator.CustomOperator;
import stringcalculator.operator.DefaultOperator;
import stringcalculator.operator.Operator;
import stringcalculator.operator.SelectedOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelectedOperatorTest {

    @DisplayName("기본 구분자 형태의 문자열이 전달되면 DefaultOperator 반환")
    @ValueSource(strings = {"1,1,1", "2:2:2", "3,3:3"})
    @ParameterizedTest
    void defaultDelimiterTest(String given) {
        assertThat(new TestSelectedOperator(given).getOperator()).isEqualTo(DefaultOperator.from(given));
    }

    @DisplayName("커스텀 구분자 형태의 문자열이 전달되면 CustomOperator 반환")
    @ValueSource(strings = {"//!\\n1!1!1", "//^\\n2^1^1", "//#\\n3#4#5"})
    @ParameterizedTest
    void customDelimiterTest(String given) {
        assertThat(new TestSelectedOperator(given).getOperator()).isEqualTo(CustomOperator.from(given));
    }

    @DisplayName("기본 구분자 형태, 커스텀 구분자 형태 둘 다 아닌 문자열이 전달되면 IllegalArgumentException throw")
    @ValueSource(strings = {"1!2@3", "//^\\n1!2!3"})
    @ParameterizedTest
    void unexpectedFormInputTest(String given) {
        assertThatThrownBy(() -> new SelectedOperator(given)).isInstanceOf(IllegalArgumentException.class);
    }

    private static class TestSelectedOperator extends SelectedOperator {

        public TestSelectedOperator(String input) {
            super(input);
        }

        public Operator getOperator() {
            return super.getOperator();
        }

    }

}
