package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * ValueSource 와 CsvSource 에서 Escape 문자열 처리가 다릅니다.
 * 이를 테스트하기 위한 학습 테스트 코드입니다.
 */
public class RegexStudyTest {

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest(name = "입력 값: {0}, 기대 값: {1}")
    @CsvSource(value = {"//;\\n1;2;3|6"}, delimiter = '|')
    public void patternEscapeInCsvSource(final String expression) {
        Matcher m = Pattern.compile("//(.*?)\\\\n").matcher(expression);

        assertThat(m.find()).isTrue();
    }


    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\\n1;2;3"})
    void patternEscapeInValueSource(final String expression) {
        Matcher m = Pattern.compile("//(.)\\\\n(.*)").matcher(expression);

        assertThat(m.find()).isTrue();
    }
}
