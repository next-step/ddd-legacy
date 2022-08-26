package addstring;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setup() {
        stringCalculator = new StringCalculator();
    }

    @ValueSource(strings = {"1,2"})
    @ParameterizedTest
    void calculate_string_general_success(String s) {
        int result = stringCalculator.add(s);
        assertThat(result).isEqualTo(3);
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text) {
        assertThat(stringCalculator.add(text)).isSameAs(Integer.parseInt(text));
    }

    @DisplayName("빈 문자열이나 null일 경우 0을 반환한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void calculate_string_empty_and_null(String s) {
        int result = stringCalculator.add(s);
        assertThat(result).isEqualTo(0);
    }


    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ValueSource(strings = {"//;\n1;2;3"})
    @ParameterizedTest
    void calculate_string_general_with_custom_separator(String s) {
        int result = stringCalculator.add(s);
        assertThat(result).isEqualTo(6);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @ValueSource(strings = {"//;\n-1;2;3"})
    @ParameterizedTest
    void calculate_string_negative_number(String s) {
        StringCalculator stringCalculator = new StringCalculator();
        assertThatThrownBy(
            () -> stringCalculator.add(s)
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void regexTest() {
        String text = "//!\n-1!2!3";
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
        }
    }
}
