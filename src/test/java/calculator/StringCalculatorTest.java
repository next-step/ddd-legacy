package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.regex.Pattern;

@DisplayName(value = "문자열 계산기 테스트")
class StringCalculatorTest {

    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator(new InputValidator(), new DelimiterParser());
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        Assertions.assertThat(calculator.add(text)).isEqualTo(0);
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "10", "100"})
    void oneNumber(final String text) {
        Assertions.assertThat(calculator.add(text)).isEqualTo(Integer.parseInt(text));
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2", "2,3", "3,4", "10,20", "100,200"})
    void twoNumbers(final String text) {
        String[] numbers = text.split(",");
        int expected = Integer.parseInt(numbers[0]) + Integer.parseInt(numbers[1]);
        Assertions.assertThat(calculator.add(text)).isEqualTo(expected);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "1,2:3",
            "1:2,3",
            "1:2:3",
            "10,20:30",
            "100:200,300"
    })
    void colons(final String text) {
        int expected = Arrays.stream(text.split("[,:]"))
                .mapToInt(Integer::parseInt)
                .sum();
        Assertions.assertThat(calculator.add(text)).isEqualTo(expected);
    }

    @DisplayName(value = "숫자가 아닌 값이 포함된 경우 IllegalArgumentException 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "b,2:3",
            "1,a:3",
            "1,2:c",
            "a:b:c",
            "1,@:3",
            "1,2:three"
    })
    void invalidInput(final String text) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> calculator.add(text))
                .withMessage("숫자가 아닌 값이 포함되어 있습니다.");
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "//;\n1;2;3",
            "//;\n10;20;30",
            "//;\n100;200;300"
    })
    void customDelimiter(final String text) {
        String[] parts = text.split("\n");
        String numbers = parts[1];
        String delimiter = String.valueOf(parts[0].charAt(2));

        int expected = Arrays.stream(numbers.split(delimiter))
                .mapToInt(Integer::parseInt)
                .sum();
        Assertions.assertThat(calculator.add(text)).isEqualTo(expected);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "//.\n1.2.3",
            "//.\n10.20.30",
            "//.\n100.200.300"
    })
    void customDelimiterDot(final String text) {
        String[] parts = text.split("\n");
        String numbers = parts[1];
        String delimiter = String.valueOf(parts[0].charAt(2));

        int expected = Arrays.stream(numbers.split(Pattern.quote(delimiter)))
                .mapToInt(Integer::parseInt)
                .sum();
        Assertions.assertThat(calculator.add(text)).isEqualTo(expected);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-10", "-100", "1,-2", "1,2,-3", "//;\n1;-2;3"})
    void negative(final String text) {
        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.add(text));
    }
}
