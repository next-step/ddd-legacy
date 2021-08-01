package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;


class OperationsTest {

    @DisplayName("int 배열을 더하기 연산한다")
    @ParameterizedTest
    @CsvSource(value = {"1:2:3,6", "2:3:4,9"}, delimiter = ',')
    void sum(String text, int check) {
        int[] tokens = Arrays.stream(text.split(":")).mapToInt(Integer::parseInt).toArray();
        int result = Operations.sum(tokens);

        assertThat(result).isSameAs(check);
    }
}