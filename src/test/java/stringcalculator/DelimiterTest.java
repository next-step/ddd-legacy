package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DelimiterTest {

    @ParameterizedTest
    @DisplayName(value = "구분자 , 혹은 : 문자열을 전달하면 String[] 로 반환된다.")
    @ValueSource(strings = {"1,2:3", "1,2,3", "1:2:3"})
    void defaultDelimiterSplit(String text) {
        String[] check = {"1","2","3"};
        String[] result = Delimiter.textToTokens(text);

        assertArrayEquals(result, check);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자 문자열을 전달하면 String[] 로 반환된다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3", "//!\n1!2!3", "//@\n1@2@3"})
    void customDelimiterSplit(String text) {
        String[] check = {"1","2","3"};
        String[] result = Delimiter.textToTokens(text);

        assertArrayEquals(result, check);
    }
}