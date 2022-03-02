package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class NumbersTest {

    @DisplayName(value = "Numbers 클래스에 더하기 기능 테스트")
    @ParameterizedTest
    @CsvSource(value = {"1,2,3", "1,1,2", "0,1,1"}, delimiter = ',')
    void sumNumbers(String firstNumber, String secondNumber, int sumNumber) {
        String[] numbers = {firstNumber, secondNumber};
        assertThat(Numbers.create(numbers).sum()).isEqualTo(sumNumber);
    }
}
