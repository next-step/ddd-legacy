package calculator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class PositiveNumbersTest {

    @DisplayName("양의 숫자 리스트의 합을 계산한다.")
    @Test
    void calculates_sum_of_positive_numbers() {
        // given
        List<String> input = Arrays.asList("1","2","3","4","5");

        // when
        PositiveNumbers positiveNumbers = new PositiveNumbers(input);

        // then
        assertEquals(15, positiveNumbers.sum());
    }
    @DisplayName("숫자가 포함되지 않으면 합은 0이다.")
    @Test
    void empty_list_returns_sum_zero() {
        // given
        List<String> input = Arrays.asList();

        // when
        PositiveNumbers positiveNumbers = new PositiveNumbers(input);

        // then
        assertEquals(0, positiveNumbers.sum());
    }
}
