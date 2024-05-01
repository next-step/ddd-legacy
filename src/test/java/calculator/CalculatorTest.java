package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;

public class CalculatorTest {

    @ParameterizedTest(name = "쉼표 또는 콜론을 구분자를 가지는 {0} 문자열을 덧셈 계산한다")
    @ValueSource(strings = {
            "1,2:3",
            "1,2,3",
            "1:2:3"
    })
    void add(String input) {
        Calculator<Integer, String> calculator = createCalculator();
        assertThat(calculator.calculate(input)).isEqualTo(6);
    }

    @ParameterizedTest(name = "커스텀 구분자를 가지는 {0} 문자열을 덧셈 계산한다")
    @ValueSource(strings = {
            "//;\n1;2;3",
            "//;\n1,2:3",
            "//;\n1,2,3",
    })
    void custom_add(String input) {
        Calculator<Integer, String> calculator = createCalculator();
        assertThat(calculator.calculate(input)).isEqualTo(6);
    }

    @DisplayName("null 혹은 빈 문자열을 덧셈 계산하면 결과는 0 이다")
    @ParameterizedTest
    @NullAndEmptySource
    void empty_add(String input) {
        Calculator<Integer, String> calculator = createCalculator();
        assertThat(calculator.calculate(input)).isZero();
    }

    @DisplayName("숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {
            "1,a,3",
            "1,2:-3",
            "//;\n1;2;-3",
            "//;\n1;a;-3",
    })
    void exception_add(String input) {
        assertThatRuntimeException()
                .isThrownBy(() -> createCalculator().calculate(input));
    }

    private Calculator<Integer, String> createCalculator() {
        return (input) -> {
            if (input == null || "".equals(input)) {
                return 0;
            }
            Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(input);
            String delimiter = ",|:";
            if (matcher.find()) {
                delimiter += "|" + matcher.group(1);
                input = matcher.group(2);
            }
            String[] parts = input.split(delimiter);
            boolean isValidPattern = Arrays.stream(parts)
                    .flatMapToInt(String::chars)
                    .allMatch(Character::isDigit);
            if (!isValidPattern) {
                throw new RuntimeException("숫자 이외의 값은 허용하지 않습니다.");
            }
            return Arrays.stream(parts)
                    .mapToInt(Integer::parseInt)
                    .sum();
        };
    }
}
