package calculator;

import calculator.exception.NegativeNumberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PositiveNumberTest {

    @DisplayName(value = "양수로 PositiveNumber 를 생성할수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"1,2"}, delimiter = ',')
    void createPositiveNumber(int number) {
        assertThat(PositiveNumber.create(number).getNumber()).isEqualTo(number);
    }

    @DisplayName(value = "음수로 PositiveNumber를 생성시도하면 NegativeNumberException 예외 처리를 한다.")
    @Test
    void createNegativeNumber() {
        assertThatExceptionOfType(NegativeNumberException.class)
                .isThrownBy(() -> PositiveNumber.create(-1));
    }
}
