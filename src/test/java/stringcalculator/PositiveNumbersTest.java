package stringcalculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositiveNumbersTest {

    private final PositiveNumber one = new PositiveNumber(1);
    private final PositiveNumber two = new PositiveNumber(2);
    private final PositiveNumber three = new PositiveNumber(3);

    @Test
    @DisplayName("PositiveNumber 들의 합을 반환한다.")
    void sum() {
        assertThat(new PositiveNumbers(one, two, three).sum())
            .isEqualTo(new PositiveNumber(6));
    }

    @Test
    @DisplayName("비어있는 경우 합은 0이다.")
    void empty() {
        assertThat(new PositiveNumbers().sum())
            .isEqualTo(PositiveNumber.ZERO);
    }
}