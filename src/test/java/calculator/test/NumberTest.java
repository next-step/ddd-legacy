package calculator.test;

import calculator.source.Number;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


public class NumberTest {

    @ParameterizedTest
    @ValueSource(strings = {"0", "100"})
    void 값은_음수가_아니다(String input) {
        assertThatNoException()
                .isThrownBy(() -> new Number(input));

    }

    @Test
    void 값이_음수면_RuntimeException_예외를_던진다() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new Number("-1"));

    }

}
