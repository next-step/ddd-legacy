package kitchenpos.calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringCalculatorTest {
    private  StringCalculator sut;

    @BeforeEach
    void setUp() {
        sut = new StringCalculator();
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
}