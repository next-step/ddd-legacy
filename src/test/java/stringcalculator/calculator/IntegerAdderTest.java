package stringcalculator.calculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stringcalculator.ValidatedNumbers;

class IntegerAdderTest {
    Calculator integerAdder = IntegerAdder.getInstance();

    @Test
    @DisplayName("정수의 합계를 구한다")
    void test0() {
        ValidatedNumbers validatedNumbers = ValidatedNumbers.of(Arrays.asList(
            1, 2, 3, 4, 5
        ));
        int result = integerAdder.execute(validatedNumbers);

        int expected = 15;
        assertThat(result).isEqualTo(expected);

    }

    @Test
    @DisplayName("음수가 포함된 정수의 합계를 구한다")
    void test1() {
        ValidatedNumbers validatedNumbers = ValidatedNumbers.of(Arrays.asList(
            1, -1, 0, 2, 3, 6
        ));
        int result = integerAdder.execute(validatedNumbers);

        int expected = 11;
        assertThat(result).isEqualTo(expected);

    }

    @Test
    @DisplayName("null 은 무시하고 정수의 합계를 구한다")
    void test2() {
        ValidatedNumbers validatedNumbers = ValidatedNumbers.of(Arrays.asList(
            1, 2, 3, null, -1
        ));
        int result = integerAdder.execute(validatedNumbers);

        int expected = 5;
        assertThat(result).isEqualTo(expected);

    }
}
