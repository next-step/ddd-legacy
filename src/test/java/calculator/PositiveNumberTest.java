package calculator;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("양수 클래스 테스트")
class PositiveNumberTest {

    @DisplayName("음수를 가질수 없다")
    @Test
    void cannot_have_negative_numbers() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PositiveNumber(-1));
    }

    @DisplayName("문자열 음수를 가질수 없다")
    @Test
    void string_cannot_have_negative_numbers() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PositiveNumber("-1"));
    }

    @DisplayName("숫자가 아닌 문자열을 가질수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"a", "b"})
    void cannot_have_native_string(String stringNumber) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PositiveNumber(stringNumber));
    }

    @DisplayName("공백, null을 가질수 없다")
    @ParameterizedTest
    @NullAndEmptySource
    void cannot_have_null_and_Empty(String stringNumber) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new PositiveNumber(stringNumber));
    }

    @DisplayName("계산할 숫자를 넣으면 숫자의 합을 반환 한다.")
    @Test
    void input_number_then_sum_nember() {
        PositiveNumber source = new PositiveNumber(3);

        final PositiveNumber result = source.add(new PositiveNumber(5));

        assertThat(result.getNumber()).isEqualTo(8);
    }

}
