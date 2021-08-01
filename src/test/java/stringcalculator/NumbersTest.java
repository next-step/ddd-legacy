package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class NumbersTest {

    @DisplayName(value = "String[]을 int[]로 반환된다.")
    @Test
    void defaultDelimiterSplit() {
        int[] check = {1,2,3};
        int[] result = Numbers.toNumbers(new String[]{"1", "2", "3"});

        assertArrayEquals(result, check);
    }

    @DisplayName(value = "음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> Numbers.toNumbers(new String[]{"-1"}));
    }
}