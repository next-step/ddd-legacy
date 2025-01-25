package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static calculator.SimpleDelimiterParser.DEFAULT_DELIMITERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SimpleDelimiterParserTest {

    private SimpleDelimiterParser customDelimiterExtractor;


    @BeforeEach
    void setup() {
        customDelimiterExtractor = new SimpleDelimiterParser();
    }

    @Test
    @DisplayName("`//`와 `\\n` 사이의 문자를 구분자에 추가한다.")
    void testExtractCustomDelimiter() {
        // given
        final String text = "//;\\n1,2:3;4";

        // when
        final InputText inputText = customDelimiterExtractor.parseInputText(text);

        // then
        assertThat(inputText).isNotNull();
        assertAll(
                () -> assertThat(inputText.numberText()).isEqualTo("1,2:3;4"),
                () -> assertThat(inputText.delimiters()).isEqualTo(DEFAULT_DELIMITERS + ';')
        );
    }

    @Test
    @DisplayName("커스텀 구분자가 없으면 기본 구분자를 그대로 사용한다.")
    void testReturnDefaultDelimiters() {
        // given
        final String text = "1,2:3";

        // when
        final InputText inputText = customDelimiterExtractor.parseInputText(text);

        // then
        assertThat(inputText).isNotNull();
        assertAll(
                () -> assertThat(inputText.numberText()).isEqualTo("1,2:3"),
                () -> assertThat(inputText.delimiters()).isEqualTo(DEFAULT_DELIMITERS)
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("입력이 null 또는 빈 문자열일 경우 numberText는 빈 문자열이 된다.")
    void testNullAndEmptyText(final String text) {
        // when
        final InputText inputText = customDelimiterExtractor.parseInputText(text);

        // then
        assertThat(inputText).isNotNull();
        assertAll(
                () -> assertThat(inputText.numberText()).isEmpty(),
                () -> assertThat(inputText.delimiters()).isEqualTo(DEFAULT_DELIMITERS)
        );
    }

}
