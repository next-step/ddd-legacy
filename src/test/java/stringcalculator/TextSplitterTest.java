package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TextSplitterTest {

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text) {
        assertThat(TextSplitter.split("1")).isEqualTo(new String[]{"1"});
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 문자형 숫자 리스트를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text) {
        assertThat(TextSplitter.split(text)).isEqualTo(new String[]{"1", "2"});
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text) {
        assertThat(TextSplitter.split(text)).isEqualTo(new String[]{"1", "2", "3"});
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(TextSplitter.split(text)).isEqualTo(new String[]{"1", "2", "3"});
    }
}
