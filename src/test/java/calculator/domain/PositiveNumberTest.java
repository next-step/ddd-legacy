package calculator.domain;

import calculator.exception.NegativeInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositiveNumberTest {

    @DisplayName("음수를 넣을 시, 에러를 발생시킨다.")
    @Test
    void negativeExceptionTest() {
        assertThatThrownBy(() -> {
            new PositiveNumber(-1);
        }).isInstanceOf(NegativeInputException.class);
    }

    @DisplayName("더한 값의 합계를 반환한다.")
    @ParameterizedTest
    @CsvSource( value = {
            "1:2:3",
            "0:0:0",
            "4:1:5"
    }, delimiter = ':')
    void sumTest(int firstValue, int secondValue, int result) {
        int sum = new PositiveNumber(firstValue).sum(new PositiveNumber(secondValue)).getNumber();

        assertThat(sum).isEqualTo(result);
    }

}