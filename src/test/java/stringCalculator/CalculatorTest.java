package stringCalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

//쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환
// (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)

//앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다.
// 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다.
// 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.

//문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.


public class CalculatorTest {
    private CustomDelimiterCondition condition;

    @BeforeEach
    void setUp() {
        condition = new CustomDelimiterCondition("//", "\\n");
    }

    @Test
    @DisplayName("with custom delimiter, should return sum value")
    void getCustomDelimiterTest() {
        String word = "//;\\n1;2;3";
        var calc = new Calculator(word, new String[]{",", ":"}, this.condition);

        assertEquals(6, calc.getSum());
    }


    @Test
    @DisplayName("should return delimiter")
    void findCustomDelimiterTest() {
        String word = "a;\\n1;2;3";
        var calc = new Calculator(word, new String[]{",", ":"}, this.condition);
        String res = calc.findCustomDelimiter(word, "a", "\\n");
        assertEquals(";", res);
    }

    @Test
    @DisplayName("with target word, should return sum value")
    void getSumTest() {
        String word = "1,2:3";
        var calc = new Calculator(word, new String[]{",", ":"}, this.condition);

        assertEquals(6, calc.getSum());
    }

    @Test
    @DisplayName("with target word, should return list")
    void createListTest() {
        String word = "1,2:3";
        var calc = new Calculator(word, new String[]{","}, this.condition);
        String[] res = calc.splitWord(word, Arrays.asList(",", ":"));
        assertArrayEquals(new String[]{"1", "2", "3"}, res);
    }

    @Test
    @DisplayName("valid number format should return number")
    void validFormatTest() {
        var calc = new Calculator("1", new String[]{"1"}, this.condition);

        int targetNumber = 1;
        int res = calc.getValidNumber(String.valueOf(targetNumber));
        assertEquals(targetNumber, res);
    }

    @Test
    @DisplayName("invalid value should throw error")
    void signatureTest() {
        var calc = new Calculator("1", new String[]{"1"}, this.condition);
        assertThatThrownBy(() -> calc.getValidNumber("BMW")).isInstanceOf(RuntimeException.class);
    }
}
