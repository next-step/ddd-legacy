package calculator;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;

public class StringCalculatorTest {

    @DisplayName("양의 정수가 아닌 값을 입력하면 RuntimeException이 발생해야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "a", "Z", ".", "-", ".9", "0.11", "1:-1:1"})
    void validNumber(String value) {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> StringCalculator.of(value);

        // then
        assertThatRuntimeException()
                .isThrownBy(throwingCallable);
    }

    @DisplayName("숫자 사이에 쉼표(,) 또는 콜론(:) 으로 구분된 문자열 형식이 아니면 RuntimeException이 발생해야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1;2", "3,,3", ":4", "2:2:", ",1", "2,2,"})
    void validSpliter(String value) {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> StringCalculator.of(value);

        // then
        assertThatRuntimeException()
                .isThrownBy(throwingCallable);
    }

    @DisplayName("숫자 사이에 쉼표(,) 또는 콜론(:) 으로 구분된 문자열을 입력하면 숫자의 합을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,2:3|6", "3:4:5|12", "4:2,3|9"}, delimiter = '|')
    void splitNumber(String value, int expected) {
        // when
        StringCalculator stringCalculator = StringCalculator.of(value);

        // then
        assertThat(stringCalculator.sum()).isEqualTo(expected);
    }
}
