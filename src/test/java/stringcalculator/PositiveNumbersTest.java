package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PositiveNumbersTest {

    @DisplayName("0포함 양수들의 합을 구한다")
    @Test
    void sum() {
        ZeroOrPositiveNumbers numbers = new ZeroOrPositiveNumbers(
                List.of(
                        new ZeroOrPositiveNumber(1),
                        new ZeroOrPositiveNumber(2),
                        new ZeroOrPositiveNumber(3)
                )
        );

        assertThat(numbers.sum()).isEqualTo(6);
    }

}