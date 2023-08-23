package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PositiveNumbersTest {

    @DisplayName(value = "주어진 숫자의 합을 계산한다.")
    @Test
    void sumTest() {
        List<Integer> numbers = Arrays.asList(1, 2);
        PositiveNumbers positiveNumbers = new PositiveNumbers(numbers);
        assertThat(positiveNumbers.sum())
                .isEqualTo(new PositiveNumber(3));
    }

    @DisplayName(value = "주어진 숫자의 배열이 NULL로 들어 왔을 때 계산한다.")
    @Test
    void nullTest() {
        PositiveNumbers positiveNumbers = new PositiveNumbers(null);
        assertThat(positiveNumbers.sum())
                .isEqualTo(new PositiveNumber(0));
    }
}