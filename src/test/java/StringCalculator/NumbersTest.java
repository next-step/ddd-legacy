package StringCalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class NumbersTest {

    @DisplayName("빈 Numbers의 합은 0이다")
    @Test
    void sumEmptyNumbers() {
        Numbers numbers = new Numbers(Collections.emptyList());
        assertThat(numbers.sum()).isEqualTo(0);
    }

    @DisplayName("Numbers의 합을 계산한다")
    @Test
    void sum() {
        Numbers numbers = new Numbers(Arrays.asList(
                new Number("1"),
                new Number("2"),
                new Number("3")
        ));
        assertThat(numbers.sum()).isEqualTo(6);
    }
} 