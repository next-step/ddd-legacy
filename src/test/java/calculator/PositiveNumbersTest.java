package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PositiveNumbersTest {

    @DisplayName("PositiveNumber들의 합을 구한다.")
    @Test
    void sum() {
        List<PositiveNumber> positiveNumbers = Arrays.asList(new PositiveNumber("1"), new PositiveNumber("2"), new PositiveNumber("3"));

        assertThat(new PositiveNumbers(positiveNumbers).sum()).isEqualTo(6);
    }
}