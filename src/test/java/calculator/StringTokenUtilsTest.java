package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("문자열 나누는 유틸 클래스 테스트")
public class StringTokenUtilsTest {

    @ParameterizedTest
    @DisplayName(" ', ':'의 토큰으로 문자열을 나누면 문자들이 반환된다.")
    @ValueSource(strings = {"3,4", "3:4"})
    void tokenString(String word) {
        // when
        String tokens[] = StringTokenUtils.tokenizer(word);
        // then
        assertThat(tokens).containsExactly("3", "4");
    }


    @Test
    @DisplayName("앞부분의 “//”와 “\\n” 사이에 위치하는 문자열로 문자열을 나누어 반환 된다.")
    void tokenSpecialColonString() {
        // given
        String word = "//;\n1;2;3";
        // when
        String tokens[] = StringTokenUtils.tokenizer(word);
        // then
        assertThat(tokens).containsExactly("1", "2", "3");
    }
}
