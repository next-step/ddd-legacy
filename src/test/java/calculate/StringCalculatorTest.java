package calculate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringCalculatorTest {

    @DisplayName("비어 있는 문자열을 입력할 경우 0을 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void calculate_NotNullAndNotEmpty(String text) {
        assertThat(StringCalculator.calculate(text)).isZero();
    }

    @Test
    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    void calculate_OneNumber() {
        assertThat(StringCalculator.calculate("5")).isEqualTo(5);
    }

    @DisplayName("숫자 이외의 값 또는 음수를 전달할 경우 런타임 예외가 발생한다")
    @ValueSource(strings = {
            "-1",
            "A",
            "1,A",
            "1,-2",
    })
    @ParameterizedTest
    void calculate_NaNWithNegativeNumber(String text) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> StringCalculator.calculate(text));
    }

    @DisplayName("컴마(,) 및 세미콜론(:)으로 구분된 두 숫자의 합을 반환한다")
    @CsvSource({
            "'1,2,3', 6",
            "'1:3:5', 9",
            "'4,5:6', 15",
    })
    @ParameterizedTest
    void calculate_DefaultDelimiter(String text, int sum) {
        assertThat(StringCalculator.calculate(text)).isEqualTo(sum);
    }

    @DisplayName("커스텀 구분자로 구분된 두 숫자의 합을 반환한다")
    @CsvSource({
            "'//;\n1;2;3', 6",
            "'//@\n4@5@6@7', 22",
            "'//!\n10!40', 50",
    })
    @ParameterizedTest
    void calculate_CustomDelimiter(String text, int sum) {
        assertThat(StringCalculator.calculate(text)).isEqualTo(sum);
    }

}
