package kitchenpos.calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringCalculatorTest {
    private  StringCalculator sut;

    @BeforeEach
    void setUp() {
        sut = new StringCalculator(new GreaterThanOrEqualZeroValidationStrategy());
    }

    @DisplayName("빈 값을 입력할 경우 0을 반환한다.")
    @Test
    void empty() {
        Assertions.assertThat(sut.sum("")).isEqualTo(0);
        Assertions.assertThat(sut.sum(null)).isEqualTo(0);
    }

    @DisplayName("숫자 하나를 입력할 경우 해당 숫자를 반환한다.")
    @Test
    void single() {
        Assertions.assertThat(sut.sum("2")).isEqualTo(2);
    }

    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환한다.")
    @Test
    void multiple() {
        Assertions.assertThat(sut.sum("1,2")).isEqualTo(3);
        Assertions.assertThat(sut.sum("1,2,3")).isEqualTo(6);
        Assertions.assertThat(sut.sum("1,2:3")).isEqualTo(6);
    }

    @DisplayName("앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void customDelimiter() {
        Assertions.assertThat(sut.sum("//;\n1;2;3")).isEqualTo(6);
    }

    @DisplayName("문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.")
    @Test
    void notNumberOrNegative() {
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> sut.sum("1,2,z"));
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> sut.sum("1,-1"));
    }
}
