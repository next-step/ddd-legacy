package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import calculator.exception.NegativeNumberException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayName("PositiveNumbers")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PositiveNumbersTest {

    @Test
    void 문자열배열로_PositiveNumbers를_생성할수있다() {
        List<PositiveNumber> positiveNumberList = List.of(
                new PositiveNumber(1),
                new PositiveNumber(2),
                new PositiveNumber(3));
        assertThat(new PositiveNumbers(new String[]{"1", "2", "3"})).isEqualTo(
                new PositiveNumbers(positiveNumberList));
    }

    @Test
    void PositiveNumbers를_생성할때_음수가_들어있다면_NegativeNumberException_예외처리를_한다() {
        assertThatExceptionOfType(NegativeNumberException.class)
                .isThrownBy(() -> new PositiveNumbers(new String[]{"1", "-2", "3"}));
    }

    @Test
    void PositiveNumbers를_생성할때_문자열에_숫자가_아닌값이_들어있다면_NumberFormatExceptio_예외처리를_한다() {
        assertThatExceptionOfType(NumberFormatException.class)
                .isThrownBy(() -> new PositiveNumbers(new String[]{"1", "숫자", "3"}));
    }

    @Test
    void PositiveNumbers의_총합을_계산한다() {
        PositiveNumbers positiveNumbers = new PositiveNumbers(new String[]{"1", "2", "3"});
        assertThat(positiveNumbers.sum()).isEqualTo(6);
    }
}