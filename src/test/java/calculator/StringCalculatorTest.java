package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class StringCalculatorTest {

    StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator(new OperandParser());
    }

    @Test
    void 빈_문자열을_입력하면_0을_반환받는다() {
        assertThat(calculator.add("")).isZero();
    }

    @Test
    void NULL_을_입력하면_0을_반환받는다() {
        assertThat(calculator.add(null)).isZero();
    }

    @Test
    void 숫자_하나를_문자열로_입력하면_해당_숫자를_반환받는다() {
        assertThat(calculator.add("3")).isEqualTo(3);
    }

    @ParameterizedTest
    @ValueSource(strings = {"3,4"})
    void 숫자_두개를_구분자로_입력하면_합을_반환받는다(final String value) {
        assertThat(calculator.add(value)).isEqualTo(7);
    }

    @ParameterizedTest
    @ValueSource(strings = {"3:4"})
    void 구분자를_콜론으로_사용할_수_있다(final String value) {
        assertThat(calculator.add(value)).isEqualTo(7);
    }

    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void 구분자를_커스텀_할_수_있다(final String value) {
        assertThat(calculator.add(value)).isEqualTo(6);
    }

    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;-1;3"})
    void 음수는_추가될_수_없다(final String value) {
        assertThatThrownBy(() -> calculator.add(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음수는 추가될 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"//;\n"})
    void 커스텀_지정자만_입력하면_0을_전달받는다(final String value) {
        assertThat(calculator.add(value)).isZero();
    }


}
