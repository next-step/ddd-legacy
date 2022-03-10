package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringCalculatorTest {

    private TokenFactory tokenFactory;

    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        tokenFactory = new TokenFactory();

        calculator = new StringCalculator(tokenFactory);
    }

    @DisplayName("빈 문자열 또는 null 값을 입력한 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        // given

        // when
        final int result = calculator.add(text);

        // then
        assertThat(result).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @MethodSource(value = "calculator.NumberProvider#oneZeroOrPositiveNumberProvider")
    void oneNumber(final int number) {
        // given

        // when
        final int result = calculator.add(String.valueOf(number));

        // then
        assertThat(result).isEqualTo(number);
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text) {
        // given

        // when
        final int result = calculator.add(text);

        // then
        assertThat(result).isSameAs(3);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text) {
        // given

        // when
        final int result = calculator.add(text);

        // then
        assertThat(result).isSameAs(6);
    }
    
    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        // given

        // when
        final int result = calculator.add(text);

        // then
        assertThat(result).isSameAs(6);
    }


    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @MethodSource(value = "calculator.NumberProvider#oneNegativeNumberProvider")
    void negative(final int negativeNumber) {
        // given

        // when & then
        assertThatThrownBy(() -> calculator.add(String.valueOf(negativeNumber)))
            .isInstanceOf(RuntimeException.class);
    }
}
