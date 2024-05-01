package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("SplitterUtils")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SplitterUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"가,나"})
    void 문자열을_쉼표로_구분하여_분리한다(final String text) {
        String[] token = SplitterUtils.split(text);
        assertThat(token).contains("가", "나");
    }

    @ParameterizedTest
    @ValueSource(strings = {"가:나:다", "가:나,다"})
    void 문자열을_콜론으로_구분하여_분리한다(final String text) {
        String[] token = SplitterUtils.split(text);
        assertThat(token).contains("가", "나", "다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2,3"})
    void 문자열_사이에_커스텀구분자를_지정할_수_있다(final String text) {
        String[] token = SplitterUtils.split(text);
        assertThat(token).contains("1", "2", "3");
    }
}