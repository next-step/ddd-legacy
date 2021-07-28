package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StringCalculatorTest {
    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName("빈 문자열 또는 null을 입력할 경우 0을 반환해야 한다.")
    @Test
    void nullOrEmpty() {
        assertThat(calculator.add(null)).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @Test
    void oneNumber() {
        assertThat(calculator.add("1")).isEqualTo(1);
    }

    @DisplayName("숫자 두개를 컴마(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @Test
    void twoNumber() {
        assertThat(calculator.add("1,2")).isEqualTo(3);
    }

    @Test
    void temp() {
        String[] split = "1".split(",");
        assertThat(split).isEqualTo(new String[]{"1"});
    }
}
