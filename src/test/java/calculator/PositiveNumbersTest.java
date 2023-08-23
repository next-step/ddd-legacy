package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PositiveNumbersTest {
    @DisplayName("숫자를 받아서 양수집합을 만들 수 있다")
    @Test
    void test1() {
        final List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        final PositiveNumbers positiveNumbers = PositiveNumbers.of(numbers);

        assertThat(positiveNumbers.getValues())
                .containsExactly(
                        new PositiveNumber(1), new PositiveNumber(2), new PositiveNumber(3),
                        new PositiveNumber(4), new PositiveNumber(5)
                );
    }

    @DisplayName("양수 여러개를 받아서 양수집합을 만들 수 있다")
    @Test
    void test2() {
        final List<PositiveNumber> numbers = List.of(
                new PositiveNumber(1),
                new PositiveNumber(2),
                new PositiveNumber(3),
                new PositiveNumber(4),
                new PositiveNumber(5)
        );

        final PositiveNumbers positiveNumbers = new PositiveNumbers(numbers);

        assertThat(positiveNumbers.getValues())
                .containsExactlyElementsOf(numbers);
    }

    @DisplayName("합계를 구할 수 있다")
    @Test
    void test3() {
        final List<PositiveNumber> numbers = List.of(
                new PositiveNumber(1),
                new PositiveNumber(2),
                new PositiveNumber(3),
                new PositiveNumber(4),
                new PositiveNumber(5)
        );
        final PositiveNumbers positiveNumbers = new PositiveNumbers(numbers);

        final PositiveNumber result = positiveNumbers.sum();

        assertThat(result).isEqualTo(new PositiveNumber(15));
    }
}
