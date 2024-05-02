package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class StringSplitterTest {

    private StringSplitter splitter;

    @BeforeEach
    void setUp() {
        splitter = new StringSplitter();
    }

    @ParameterizedTest
    @DisplayName("쉼표(,)와 콜론(:) 구분자로 문자열을 나눈다.")
    @ValueSource(strings = {"1,2:3"})
    void split(final String input) {
        final var tokens = splitter.split(input);
        assertThat(tokens).containsExactly("1", "2", "3");
    }

    @ParameterizedTest
    @DisplayName("//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ValueSource(strings = {"//;\n1;2;3"})
    void splitWithCustomDelimiter(final String input) {
        final var split = splitter.split(input);
        assertThat(split).containsExactly("1", "2", "3");
    }
}
