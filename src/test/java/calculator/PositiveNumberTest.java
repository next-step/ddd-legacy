package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PositiveNumberTest {

    @DisplayName(value = "음수가 될 수 없다")
    @Test
    void constructor1() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveNumber(-1));
    }

    @DisplayName(value = "0 또는 양수가 될 수 있다")
    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void constructor2(int number) {
        PositiveNumber actual = new PositiveNumber(number);

        assertThat(actual.value()).isEqualTo(number);
    }

    @DisplayName("더할 수 있다")
    @Test
    void plus() {
        PositiveNumber positiveNumber1 = new PositiveNumber(1);
        PositiveNumber positiveNumber2 = new PositiveNumber(2);

        PositiveNumber actual = positiveNumber1.plus(positiveNumber2);

        assertThat(actual.value()).isEqualTo(3);
    }
}
