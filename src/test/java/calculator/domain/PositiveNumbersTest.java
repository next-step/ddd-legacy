package calculator.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class PositiveNumbersTest {

    private PositiveNumbers positiveNumbers;

    @BeforeEach
    void setUp() {
        this.positiveNumbers = new PositiveNumbers(
                List.of(new PositiveNumber(1),
                        new PositiveNumber(2))
        );
    }

    @DisplayName("1과 2을 더하면 3이 나온다.")
    @Test
    void sumTest() {
        int sum = positiveNumbers.sum();

        assertThat(sum).isEqualTo(3);
    }

    @DisplayName("PositiveNumbers가 null일 경우 에러를 발생한다.")
    @Test
    void nullReduceTest() {
        assertThatNullPointerException().isThrownBy(() -> {
            new PositiveNumbers(null);
        });
    }


}