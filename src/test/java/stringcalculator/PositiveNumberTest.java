package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ZeroOrPositiveNumberTest {

    @DisplayName("양수가 아니면 예외를 발생시킨다")
    @Test
    void constructorByPositiveNumber() {
        assertThatThrownBy(() -> new ZeroOrPositiveNumber(-1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("음수는 계산할 수 없습니다.");;
    }

    @DisplayName("문자형 숫자가 아닌 문자는 예외를 발생시킨다")
    @Test
    void constructorByText() {
        assertThatThrownBy(() -> new ZeroOrPositiveNumber("text"))
                .isInstanceOf(NumberFormatException.class)
                .hasMessage("문자형 숫자만 입력 가능합니다");
    }

    @DisplayName("합을 구한다")
    @Test
    void sum() {
        ZeroOrPositiveNumber number1 = new ZeroOrPositiveNumber(1);
        ZeroOrPositiveNumber number2 = new ZeroOrPositiveNumber(2);

        assertThat(number1.sum(number2)).isEqualTo(new ZeroOrPositiveNumber(3));
    }
}
