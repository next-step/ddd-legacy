package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatException;

class NumberValidatorTest {

    private NumberValidator numberValidator;


    @BeforeEach
    void setup() {
        numberValidator = new NumberValidator();
    }


    @Test
    @DisplayName("양의 정수로 이루어진 문자열 배열인지 검증한다.")
    void testValidString() {
        // given
        final String[] strings = {"1", "23", "45"};

        // when & then
        assertThatCode(() -> numberValidator.validateNumbers(strings))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("음수 문자열이 있으면 예외를 발생한다.")
    void testNegativeNumberString() {
        // given
        final String[] strings = {"-1", "23", "45"};

        // when & then
        assertThatException()
                .isThrownBy(() -> numberValidator.validateNumbers(strings))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("숫자가 아닌 문자열이 있으면 예외를 발생한다.")
    void testNonNumericString() {
        // given
        final String[] strings = {"a", "23", "45"};

        // when & then
        assertThatException()
                .isThrownBy(() -> numberValidator.validateNumbers(strings))
                .isInstanceOf(RuntimeException.class);
    }

}
