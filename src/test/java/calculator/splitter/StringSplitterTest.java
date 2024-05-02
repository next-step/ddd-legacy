package calculator.splitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import calculator.splitter.Splitter;
import calculator.splitter.StringSplitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("StringSplitter")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StringSplitterTest {

    private Splitter<String> splitter;

    @BeforeEach
    void setUp() {
        splitter = new StringSplitter();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 빈문자열_또는_null값을_입력할경우_IllegalArgumentException_예외처리를_한다(final String text) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> splitter.split(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"가,나"})
    void 문자열을_쉼표로_구분하여_분리한다(final String text) {
        String[] token = splitter.split(text);
        assertThat(token).contains("가", "나");
    }

    @ParameterizedTest
    @ValueSource(strings = {"가:나:다", "가:나,다"})
    void 문자열을_콜론으로_구분하여_분리한다(final String text) {
        String[] token = splitter.split(text);
        assertThat(token).contains("가", "나", "다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3", "//&\n1&2&3", "//;\n1;2,3"})
    void 문자열_사이에_커스텀구분자를_지정할_수_있다(final String text) {
        String[] token = splitter.split(text);
        assertThat(token).contains("1", "2", "3");
    }
}