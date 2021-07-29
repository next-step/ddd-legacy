package study1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositiveNumberTest {

    @Test
    @DisplayName("문자열 변환 결과가 음수일 경우 생성 불가")
    void fail_negativeNumber() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> new PositiveNumber("-1"));
    }

    @Test
    @DisplayName("숫자가 아닌 문자열이 입력 될 경우 생성 불가")
    void fail_isNotNumeric() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> new PositiveNumber("a"));
    }

    @Test
    @DisplayName("문자열 덧셈 결과 확인")
    void success_add() {
        // given
        PositiveNumber number1 = new PositiveNumber("12");
        PositiveNumber number2 = new PositiveNumber("20");
        PositiveNumber expected = new PositiveNumber("32");

        // when
        PositiveNumber result = number1.add(number2);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
