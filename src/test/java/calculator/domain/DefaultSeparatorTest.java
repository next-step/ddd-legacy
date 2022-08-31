package calculator.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DefaultSeparatorTest {

    private Separator separator;

    @BeforeEach
    void setUp() {
        separator = new DefaultSeparator();
    }

    @DisplayName("주어진 문자열이 커스텀 구분자 정규식 패턴과 일치하는지를 포함했는지 아닌지를 판별할 때 무조건 false를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2,3", "1:3:10", "//;\n"})
    void isMatchWithTextShouldBeFalse(String text) {
        assertThat(separator.isMatchWithText(text)).isFalse();
    }

    @DisplayName("기본 구분자를 통해 문자열을 나눌 수 있다.")
    @Test
    void split() {
        // given
        String text = "3:5:2";

        // when
        List<String> result = separator.split(text);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("3", "5", "2");
    }
}
