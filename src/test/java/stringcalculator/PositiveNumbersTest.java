package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PositiveNumbersTest {

    @DisplayName("숫자의 합을 리턴한다.")
    @Test
    void sum() {
        final PositiveNumber positiveNumber1 = new PositiveNumber(1);
        final PositiveNumber positiveNumber2 = new PositiveNumber(2);
        final PositiveNumber positiveNumber3 = new PositiveNumber(3);
        final List<PositiveNumber> numbers = Arrays.asList(positiveNumber1, positiveNumber2, positiveNumber3);
        final PositiveNumbers positiveNumbers = new PositiveNumbers(numbers);

        final PositiveNumber actual = positiveNumbers.sum();

        assertThat(actual).isEqualTo(new PositiveNumber(6));
    }
}
