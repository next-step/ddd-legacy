package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class StringTokenUtilsTest {


    @ParameterizedTest
    @DisplayName(" ', ':'문자열으로 구분한다.")
    @ValueSource(strings = {"3,4", "3:4"})
    void tokenString(String word) {
        // when
        String tokens[] = StringTokenUtils.tokenizer(word);
        // then
        assertThat(tokens).containsExactly("3", "4");
    }


    @Test
    @DisplayName("특정 토큰으로 문자열으로 구분한다.")
    void tokenSpecialColonString() {
        // given
        String word = "//;\n1;2;3";
        // when
        String tokens[] = StringTokenUtils.tokenizer(word);
        // then
        assertThat(tokens).containsExactly("1", "2", "3");
    }
}
