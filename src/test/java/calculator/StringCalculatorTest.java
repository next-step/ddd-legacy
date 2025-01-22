package calculator;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatRuntimeException;

public class StringCalculatorTest {

    @DisplayName("양의 정수가 아닌 값을 입력하면 RuntimeException이 발생해야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "a", "Z", ".", "-", ".9", "0.11"})
    void validNumber(String value) {
        // given

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new StringCalculator("-1");

        // then
        assertThatRuntimeException()
                .isThrownBy(throwingCallable);
    }
}
