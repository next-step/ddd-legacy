package calculator.test;

import calculator.Numbers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NumbersTest {
    @DisplayName("숫자들의 합을 구한다.")
    @Test
    void sum() {
        Numbers numbers = new Numbers(new String[]{"1", "2", "3"});
        assertThat(numbers.sum()).isEqualTo(6);
    }
}
