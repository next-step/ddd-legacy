package stringCalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
