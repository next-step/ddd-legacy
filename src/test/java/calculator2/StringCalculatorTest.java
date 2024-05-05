package calculator2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class StringCalculatorTest {

    @NullAndEmptySource
    @ParameterizedTest
    @DisplayName("입력값이 없거나 null 이면 0이 반환된다.")
    void test1(String input) {
        //Arrange
        StringCalculator calculator = new StringCalculator();

        //Act
        int result = calculator.add(input);

        //Assert
        assertThat(result).isEqualTo(0);
    }

    @ValueSource(strings = {"1", "2", "3"})
    @ParameterizedTest
    @DisplayName("입력값이 1개인 경우 해당 값이 반환된다.")
    void test2(String input) {
        //Arrange
        StringCalculator calculator = new StringCalculator();

        //Act
        int result = calculator.add(input);

        //Assert
        assertThat(result).isEqualTo(Integer.parseInt(input));
    }

    @ValueSource(strings = {"3,3", "1,2,3", "1,2:3"})
    @ParameterizedTest
    @DisplayName("문자열에 쉼표 또는 콜론이 구분자로 있으면, 구분자를 기준으로 분리한 각 숫자의 합을 반환한다.")
    void testAdd_withCommaAndColonDelimiters(String input) {
        //Arrange
        StringCalculator calculator = new StringCalculator();

        //Act
        int actual = calculator.add(input);

        //Assert
        assertThat(actual).isEqualTo(6);
    }

    @ValueSource(strings = {"//;\n1;2;3", "//.\n1.2.3", "//*\n1*2*3"})
    @ParameterizedTest
    @DisplayName("커스텀 구분자를 지정할 수 있고, 결과 값은 분리된 숫자들의 합이다.")
    void testAdd_withCustomDelimiter(String input) {
        //Arrange
        StringCalculator calculator = new StringCalculator();

        //Act
        int actual = calculator.add(input);

        //Assert
        assertThat(actual).isEqualTo(6);
    }

    @ValueSource(strings = {"a", "1,a", "4:a", "//$\n1$a"})
    @ParameterizedTest
    @DisplayName("문자열 계산기에 숫자 이외의 값이 전달된 경우 RuntimeException을 throw 한다.")
    void testAdd_withNonNumericValue(String input) {
        //Arrange
        StringCalculator calculator = new StringCalculator();

        //Assert
        assertThatCode(() -> calculator.add(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid number: a");
    }

    @ValueSource(strings = {"-1", "-1,2", "3:-5", "//;\n-1;2;3"})
    @ParameterizedTest
    @DisplayName("문자열 계산기에 음수를 전달하는 경우 RuntimeException을 throw 한다.")
    void testAdd_withNegativeNumber(String input) {
        //Arrange
        StringCalculator calculator = new StringCalculator();

        //Assert
        assertThatCode(() -> calculator.add(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Number must me positive.");
    }
}
