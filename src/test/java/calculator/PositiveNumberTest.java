package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PositiveNumberTest {
    @DisplayName("양수로 생성하면 성공한다")
    @Test
    void test1() {
        final PositiveNumber positiveNumber = new PositiveNumber(1);

        assertThat(positiveNumber.getValue()).isEqualTo(1);
    }

    @DisplayName("음수로 양수를 생성하면 실패한다")
    @Test
    void test2() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveNumber(-1));
    }
}
