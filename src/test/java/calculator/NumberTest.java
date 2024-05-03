package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class NumberTest {

    @DisplayName("생성된다")
    @Test
    void createTest() {

        Assertions.assertThatCode(() -> new Number(0))
                .doesNotThrowAnyException();
    }


    @DisplayName("숫자 이외의 문자열로 생성하면 실패한다")
    @Test
    void createFailedWhenInputNonNumberString() {

        assertThrowsExactly(IllegalArgumentException.class, () -> Number.of("a"));
    }

    @DisplayName("음수는 Number생성 실패한다")
    @Test
    void createNumberFailedWhenNegative() {
        Assertions.assertThatThrownBy(() -> new Number(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("동등성 테스트")
    @Test
    void equalsTest() {
        assertThat(new Number(0)).isEqualTo(new Number(0));
    }
}
