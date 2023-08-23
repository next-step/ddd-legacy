package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PositiveNumberTest {


    @DisplayName("value가 음수라면 RuntimeException을 발생시킨다")
    @ParameterizedTest
    @ValueSource(ints = -1)
    void constructor(final int value) {

        // when & then
        assertThatThrownBy(() -> new PositiveNumber(value))
            .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("PositiveNumber를 받아서 더한 값을 반환한다")
    @Test
    void sum() {
        // given
        final PositiveNumber positiveNumber = new PositiveNumber(5);
        final PositiveNumber positiveNumeber2 = new PositiveNumber(10);

        // when
        final PositiveNumber actual = positiveNumber.sum(positiveNumeber2);

        // then
        final PositiveNumber expect = new PositiveNumber(15);
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(expect);
    }
}