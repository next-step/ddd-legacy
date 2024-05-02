package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import calculator.exception.NegativeNumberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("PositiveNumber")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PositiveNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-2", "-100"})
    void 음수문자열로_PositiveNumber를_생성하면_NegativeNumberException_예외처리를_한다(String token) {
        assertThatExceptionOfType(NegativeNumberException.class)
                .isThrownBy(() -> new PositiveNumber(token));
    }

    @Test
    void 문자열로_PositiveNumber를_생성할수있다() {
        PositiveNumber positiveNumber = new PositiveNumber("10");
        assertThat(positiveNumber).isEqualTo(new PositiveNumber(10));
    }
}