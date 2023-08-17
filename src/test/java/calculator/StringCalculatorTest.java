package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringCalculatorTest {

    StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @Test
    void 빈_문자열을_입력하면_0을_반환받는다() {
        Assertions.assertThat(calculator.add("")).isZero();
    }

    @Test
    void NULL_을_입력하면_0을_반환받는다() {
        Assertions.assertThat(calculator.add(null)).isZero();
    }
    @Test
    void 숫자_하나를_문자열로_입력하면_해당_숫자를_반환받는다() {
        Assertions.assertThat(calculator.add("3")).isEqualTo(3);
    }

    @ParameterizedTest
    @ValueSource(strings = {"3,4"})
    void 숫자_두개를_구분자로_입력하면_합을_반환받는다(final String value) {
        Assertions.assertThat(calculator.add(value)).isEqualTo(7);
    }

    @ParameterizedTest
    @ValueSource(strings = {"3:4"})
    void 구분자를_콜론으로_사용할_수_있다(final String value) {
        Assertions.assertThat(calculator.add(value)).isEqualTo(7);
    }

    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void 구분자를_커스텀_할_수_있다(final String value) {
        Assertions.assertThat(calculator.add(value)).isEqualTo(6);
    }


}
