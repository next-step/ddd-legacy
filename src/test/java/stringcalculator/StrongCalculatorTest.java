package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class StrongCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        stringCalculator = new StringCalculator();
    }

    @DisplayName("빈 문자열 또는 null 을 입력하면 결과는 0이다.")
    @NullAndEmptySource
    @ParameterizedTest
    void empty_or_null_thenZero(String input) {
        int result = stringCalculator.calculate(input);
        assertThat(result).isZero();
    }

    @DisplayName("숫자 문자열 하나 입력시 입력한 숫자 문자열을 반환한다.")
    @Test
    void one_string_sum() {
        int result = stringCalculator.calculate("1");
        assertThat(result).isEqualTo(1);
    }

    @DisplayName("숫자 두개를 컴마나 콜론으로 구분해서 입력할 경우 두 숫자의 합을 반환한다.")
    @CsvSource(value = {"1,2=3", "2,3=5", "3,7=10", "5,8=13", "1:2=3", "6:9=15"}, delimiter = '=')
    @ParameterizedTest
    void two_string_sum(String input, Integer expected) {
        int result = stringCalculator.calculate(input);
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("두개 이상의 숫자를 컴마나 콜론으로 구분해서 입력할 경우 숫자들의 합을 반환한다.")
    @CsvSource(value = {"1,2:3=6", "2,4,8=14", "3:6:9=18"}, delimiter = '=')
    @ParameterizedTest
    void strings_sum(String input, Integer expected) {
        int result = stringCalculator.calculate(input);
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("//와 \n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ValueSource(strings = {"//;\n1;2;3"})
    @ParameterizedTest
    void custom_delimiter_sum(String input) {
        int result = stringCalculator.calculate(input);
        assertThat(result).isEqualTo(6);
    }

    @DisplayName("음수가 입력될 시 예외가 발생한다.")
    @ValueSource(strings = {"-1", "1:-2", "1,3,-5"})
    @ParameterizedTest
    void negative_then_exception(String input) {
        assertThatThrownBy(() -> stringCalculator.calculate(input))
                .isInstanceOf(RuntimeException.class);
    }

}

class StringCalculator {
    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String COMMA_OR_COLON = ",|:";

    public int calculate(String input) {
        if (StringUtils.isBlank(input)) {
            return 0;
        }

        Matcher matcher = PATTERN.matcher(input);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            String[] numbers = matcher.group(2).split(customDelimiter);
            return sumStringArray(numbers);
        }

        String[] numbers = input.split(COMMA_OR_COLON);
        return sumStringArray(numbers);
    }

    private int sumStringArray(String[] numbers) {
        if (numbers.length == 1) {
            return convertNumber(numbers[0]);
        }

        return Arrays.stream(numbers)
                .mapToInt(this::convertNumber)
                .sum();
    }

    private int convertNumber(String number) {
        try {
            int result = Integer.parseInt(number);
            validate(result);
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    private void validate(int number) {
        if (number < 0) {
            throw new RuntimeException("음수를 입력할 수 없습니다.");
        }
    }
}
