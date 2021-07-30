package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class NumbersTest {

    @ParameterizedTest
    @DisplayName(value = "구분자 , 혹은 : 문자열을 전달하면 int[] 로 반환된다.")
    @ValueSource(strings = {"1,2:3", "1,2,3", "1:2:3"})
    void defaultDelimiterSplit(String text) {
        int[] check = {1,2,3};
        int[] result = Numbers.textToNumbers(text);

        assertArrayEquals(result, check);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자 문자열을 전달하면 int[] 로 반환된다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3", "//!\n1!2!3", "//@\n1@2@3"})
    void customDelimiterSplit(String text) {
        int[] check = {1,2,3};
        int[] result = Numbers.textToNumbers(text);

        assertArrayEquals(result, check);
    }

    @DisplayName(value = "음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> Numbers.textToNumbers("-1"));
    }
}