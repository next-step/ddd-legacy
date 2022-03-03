package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NumbersTest {

    @DisplayName("Number들의 합을 구한다.")
    @Test
    void sum() {
        List<Number> numbers = Arrays.asList(new Number("1"), new Number("2"), new Number("3"));

        assertThat(new Numbers(numbers).sum()).isEqualTo(6);
    }
}