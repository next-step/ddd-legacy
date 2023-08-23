package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PositiveNumberTest {

    @DisplayName(value = "주어진 객체가 동등한지 확인 한다.")
    @Test
    void equalsTest() {
        assertThat(new PositiveNumber(1))
                .isEqualTo(new PositiveNumber(1));
    }

    @DisplayName(value = "주어진 해시 코드가 같은지 확인 한다.")
    @Test
    void hashCodeTest() {
        assertThat(new PositiveNumber(1).hashCode())
                .isSameAs(new PositiveNumber(1).hashCode());
    }

    @DisplayName(value = "주어진 값이 같은지 확인 한다.")
    @Test
    void numberEqualsTest() {
        assertThat(new PositiveNumber(1).getNumber())
                .isEqualTo(1);
    }

    @DisplayName(value = "plus 계산이 제대로 동작하는지 확인 한다.")
    @Test
    void plusTest() {
        assertThat(new PositiveNumber(1).plus(new PositiveNumber(2)))
                .isEqualTo(new PositiveNumber(3));
    }
}