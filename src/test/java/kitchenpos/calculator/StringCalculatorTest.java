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
}