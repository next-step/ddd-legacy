package stringcalculator.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stringcalculator.ParsedNumbers;

class NonNegativeIntegerValidatorTest {
    Validator nonNegativeIntegerValidator = NonNegativeIntegerValidator.getInstance();

    @Test
    @DisplayName("파싱된 정수들이 0이상의 정수로 이루어졌다면 결과 정수 리스트와 같다")
    void test0() {
        ParsedNumbers parsedNumbers = ParsedNumbers.of(Arrays.asList(
            0, 1, 2, 3, 4, 5
        ));
        List<Integer> result = nonNegativeIntegerValidator.execute(parsedNumbers).getIntegers();

        List<Integer> expected = parsedNumbers.integers();
        assertThat(result).containsExactlyElementsOf(expected);

    }

    @Test
    @DisplayName("음수가 포함된다면 IllegalArgumentException 이 발생한다")
    void test1() {
        ParsedNumbers parsedNumbers = ParsedNumbers.of(Arrays.asList(
            1, -1, 0, 2, 3, 6
        ));

        assertThrows(IllegalArgumentException.class, () -> {
            nonNegativeIntegerValidator.execute(parsedNumbers);
        });

    }

    @Test
    @DisplayName("null 이 포함된다면 IllegalArgumentException 이 발생한다")
    void test2() {
        ParsedNumbers parsedNumbers = ParsedNumbers.of(Arrays.asList(
            1, 2, 3, null, -1
        ));
        assertThrows(IllegalArgumentException.class, () -> {
            nonNegativeIntegerValidator.execute(parsedNumbers);
        });

    }
}
