package calculator.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CustomSeparatorTest {

    private Separator separator;

    @BeforeEach
    void setUp() {
        separator = new CustomSeparator();
    }

    @DisplayName("주어진 문자열이 커스텀 구분자 정규식 패턴과 일치하는지를 포함했는지 아닌지를 판별할 수 있다. - true")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n", "//a\n", "//?\n"})
    void isMatchWithTextShouldBeTrue(String text) {
        assertThat(separator.isMatchWithText(text)).isTrue();
    }

    @DisplayName("주어진 문자열이 커스텀 구분자 정규식 패턴과 일치하는지를 포함했는지 아닌지를 판별할 수 있다. - false")
    @ParameterizedTest
    @ValueSource(strings = {"/;\n", "/z\n", "//?\""})
    void isMatchWithTextShouldBeFalse(String text) {
        assertThat(separator.isMatchWithText(text)).isFalse();
    }

    @DisplayName("커스텀 구분자를 통해 문자열을 나눌 수 있다.")
    @Test
    void split() {
        // given
        String text = "//z\n3z5z2";

        // when
        List<String> result = separator.split(text);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("3", "5", "2");
    }

}
