package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringSplitorTest {

    @Test
    @DisplayName("기본 구분자(쉼표, 콜론)로 문자열을 분리할 수 있다")
    void shouldSplitStringUsingDefaultDelimiters() {
        String[] result = StringSplitor.split("1,2:3");
        assertArrayEquals(new String[]{"1", "2", "3"}, result);
    }

    @Test
    @DisplayName("커스텀 구분자로 문자열을 분리할 수 있다")
    void shouldSplitStringUsingCustomDelimiter() {
        String[] result = StringSplitor.split("//;\n1;2;3");
        assertArrayEquals(new String[]{"1", "2", "3"}, result);
    }

}