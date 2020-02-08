package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StringSumCalculatorTest {

    @DisplayName(
            value = "쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환한다."
    )
    @Test
    void split() {
        final StringSumCalculator calculator = new StringSumCalculator();
        final String case1 = "1,1,1,1";
        final int result1 = calculator.sum(case1);
        assertThat(result1).isEqualTo(4);

        final String case2 = "1:1:1:1";
        final int result2 = calculator.sum(case2);
        assertThat(result2).isEqualTo(4);

        final String case3 = "2,3,4:5";
        final int result3 = calculator.sum(case3);
        assertThat(result3).isEqualTo(14);
    }

    @DisplayName(value = "한 개의 값을 입력받았을 때는 해당 값을 반환한다.")
    @Test
    void singleItem() {
        final StringSumCalculator calculator = new StringSumCalculator();
        final String case1 = "25";
        final int result1 = calculator.sum(case1);
        assertThat(result1).isEqualTo(25);
    }

    @DisplayName(
            value = "문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다."
    )
    @ParameterizedTest
    @ValueSource(strings = {"abc", "a,c", "-1", "-3,-4,1"})
    void exception(final String testCase) {
        final StringSumCalculator calculator = new StringSumCalculator();

        assertThatThrownBy(() -> {
            calculator.sum(testCase);
        }).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다.")
    @Test
    void customDelimiter() {
        final StringSumCalculator calculator = new StringSumCalculator();

        final String case1 = "//;\n1;2;3";
        final int result1 = calculator.sum(case1);
        assertThat(result1).isEqualTo(6);

        final String case2 = "//*\n5";
        final int result2 = calculator.sum(case2);
        assertThat(result2).isEqualTo(5);
    }

    @DisplayName("커스텀 구분자가 포함된 문자열에 대해서만 커스텀 구분자를 적용한다. 커스텀 구분자를 이후에 재사용하지 않는다.")
    @Test
    void customDelimiter2() {
        final StringSumCalculator calculator = new StringSumCalculator();

        final String case1 = "//;\n1;2;3";
        final int result1 = calculator.sum(case1);
        assertThat(result1).isEqualTo(6);

        final String case2 = "1;2;3";
        assertThatThrownBy(() -> {
            calculator.sum(case2);
        }).isInstanceOf(RuntimeException.class);

        final String case3 = "1,2,3";
        final int result3 = calculator.sum(case3);
        assertThat(result3).isEqualTo(6);
    }

    @DisplayName("구분자로 분리된 토큰이 빈 문자열인 경우 0으로 취급한다.")
    @Test
    void emptyValues() {
        final StringSumCalculator calculator = new StringSumCalculator();

        final String case1 = "";
        final int result1 = calculator.sum(case1);
        assertThat(result1).isEqualTo(0);

        final String case2 = ",,3,";
        final int result2 = calculator.sum(case2);
        assertThat(result2).isEqualTo(3);

        final String case3 = ",,,,";
        final int result3 = calculator.sum(case3);
        assertThat(result3).isEqualTo(0);

        final int result4 = calculator.sum(null);
        assertThat(result4).isEqualTo(0);
    }
}
