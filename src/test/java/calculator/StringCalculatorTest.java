package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import java.util.Arrays;

@DisplayName("문자열 덧셈 계산기 테스트")
public class StringCalculatorTest {
    private StringCalculator calculator;
    private SplitStrategy strategy = new StringSplitStrategy();

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName("빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.(예 : “” => 0, null => 0)")
    @ParameterizedTest
    @NullAndEmptySource
    void checkEmptyAndNull(String input) {
        Assertions.assertThat(calculator.calculate(input)).isEqualTo(0);
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.(예 : “1”)")
    @Test
    void inputOnlyOne() {
        Assertions.assertThat(calculator.calculate("1")).isEqualTo(1);
    }

    @DisplayName("숫자 두개를 컴마(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.(예 : “1,2”)")
    @Test
    void sum_splitByComma() {
        Assertions.assertThat(calculator.calculate(strategy, "1,2")).isEqualTo(3);
    }

    @DisplayName("구분자를 컴마(,) 이외에 콜론(:)을 사용할 수 있다. (예 : “1,2:3” => 6)")
    @Test
    void sum_splitByColon() {
        Assertions.assertThat(calculator.calculate(strategy, "1,2:3")).isEqualTo(6);
    }
}

class StringCalculator {

    public int calculate(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        return Integer.valueOf(input);
    }

    public int calculate(SplitStrategy strategy, String input) {
        String[] splitInput = strategy.split(input);

        if (splitInput.length == 1) {
            return Integer.valueOf(splitInput[0]);
        }

        int result = Arrays.stream(splitInput)
                .mapToInt(Integer::valueOf)
                .sum();

        return result;
    }
}

interface SplitStrategy {
    String[] split(String input);
}

class StringSplitStrategy implements SplitStrategy {

    @Override
    public String[] split(String input) {
        return input.split(",|:");
    }
}