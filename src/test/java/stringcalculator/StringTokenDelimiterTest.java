package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringTokenDelimiterTest {

    @DisplayName("숫자 두개를 comma 구분자로 입력할 경우 두 숫자의 배열을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void commaDelimiter(final String text) {
        assertThat(StringTokenDelimiter.split(text)).isEqualTo(new String[] {"1", "2"});
    }

    @DisplayName("구분자를 comma 이외에 colon 을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void commaColonDelimiter(final String text) {
        assertThat(StringTokenDelimiter.split(text)).isEqualTo(new String[] {"1", "2", "3"});
    }

    @DisplayName("\"//\"와 \"\\n\" 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(StringTokenDelimiter.split(text)).isEqualTo(new String[] {"1", "2", "3"});
    }
}
