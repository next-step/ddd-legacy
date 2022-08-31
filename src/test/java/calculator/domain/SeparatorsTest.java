package calculator.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SeparatorsTest {

    @DisplayName("문자열과 패턴이 일치하는 구분자를 사용해서 문자열을 나눈다. - 기본 구분자")
    @Test
    void splitTextByDefault() {
        // given
        Separators separators = Separators.generate();
        String text = "3:5:2";

        // when
        List<String> result = separators.splitText(text);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("3", "5", "2");
    }

    @DisplayName("문자열과 패턴이 일치하는 구분자를 사용해서 문자열을 나눈다. - 커스텀 구분자")
    @Test
    void splitTextByCustom() {
        // given
        Separators separators = Separators.generate();
        String text = "//;\n3;5;2";

        // when
        List<String> result = separators.splitText(text);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("3", "5", "2");
    }
}
