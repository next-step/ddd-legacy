package calculator;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CalculatorNumberTest {

    @DisplayName("음수를 가질수 없다")
    @Test
    void cannot_have_negative_numbers() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new CalculatorNumber(-1));
    }

    @DisplayName("문자열 음수를 가질수 없다")
    @Test
    void string_cannot_have_negative_numbers() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new CalculatorNumber("-1"));
    }

    @DisplayName("숫자가 아닌 문자열을 가질수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"a", "b"})
    void cannot_have_native_string(String stringNumber) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new CalculatorNumber(stringNumber));
    }

    @DisplayName("공백, null을 가질수 없다")
    @ParameterizedTest
    @NullAndEmptySource
    void cannot_have_null_and_Empty(String stringNumber) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new CalculatorNumber(stringNumber));
    }

    @DisplayName("계산할 숫자를 더한다")
    @Test
    void add_number() {
        CalculatorNumber source = new CalculatorNumber(3);

        source.add(new CalculatorNumber(5));

        assertThat(source.getNumber()).isEqualTo(8);
    }

}
