package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DelimiterTest {

    @DisplayName(value = "기본 구분자를 기반으로 문자열 분리")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void splitBasicDelimiter(final String text) {
        assertThat(Delimiter.split(text)).isEqualTo(new String[]{"1", "2", "3"});
    }

    @DisplayName(value = "커스텀 구분자를 기반으로 문자열 분리")
    @ParameterizedTest
    @ValueSource(strings = {"//?\n1?2?3"})
    void splitCustomDelimiter(final String text) {
        assertThat(Delimiter.split(text)).isEqualTo(new String[]{"1", "2", "3"});
    }

    @DisplayName(value = "커스텀 구분자 찾기")
    @ParameterizedTest
    @ValueSource(strings = {"//?\n1?2?3"})
    void findCustomDelimiter(final String text) {
        assertThat(Delimiter.customize(text)).isEqualTo("?");
    }

}
