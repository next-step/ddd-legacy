package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SyntaxTest {

    @DisplayName("빈 문자열의 경우 빈 문자열을 반환한다")
    @Test
    void emptyString() {
        assertThat(Syntax.parse("")).containsExactly("");
    }

    @DisplayName("쉽표(,) 또는 콜론(:)을 구분자로 사용한다.")
    void parse() {
        assertThat(Syntax.parse("1,2:3")).containsExactly("1", "2", "3");
    }

    @DisplayName("“//”와 “\\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다.")
    @Test
    void customSeparator() {
        assertThat(Syntax.parse("//;\\n1;2;3")).containsExactly("1", "2", "3");
    }
}