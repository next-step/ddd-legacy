package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTextSplitterTest {

    private SimpleTextSplitter numberExtractor;


    @BeforeEach
    void setup() {
        numberExtractor = new SimpleTextSplitter();
    }


    @Test
    @DisplayName("주어진 문자열과 구분자로 문자열을 분리한다.")
    void testSplitTextNumbers() {
        // given
        final String text = "1,2:3";
        final String delimiters = ",:";
        final InputText inputText = new InputText(text, delimiters);

        // when
        final String[] result = numberExtractor.splitText(inputText);

        // then
        assertThat(result).hasSize(3)
                .containsExactly("1", "2", "3");
    }

    @Test
    @DisplayName("구분자가 문자열에 포함되지 않아도 문자열을 올바르게 분리한다.")
    void testDelimiters() {
        // given
        final String text = "1,2:3";
        final String delimiters = ",:|";
        final InputText inputText = new InputText(text, delimiters);

        // when
        final String[] result = numberExtractor.splitText(inputText);

        // then
        assertThat(result).hasSize(3)
                .containsExactly("1", "2", "3");
    }

    @ParameterizedTest
    @DisplayName("빈문자열은 구분자 상관없이 [\"\"]을 반환한다.")
    @ValueSource(strings = {"", ",;"})
    void testEmptyText(final String delimiters) {
        // given
        final String text = "";
        final InputText inputText = new InputText(text, delimiters);

        // when
        final String[] result = numberExtractor.splitText(inputText);

        // then
        assertThat(result).hasSize(1)
                .containsExactly("");
    }

    @Test
    @DisplayName("구분자가 비었으면 문자열을 그대로 반환한다.")
    void testEmptyDelimiters() {
        // given
        final String text = "1234";
        final String delimiters = "";
        final InputText inputText = new InputText(text, delimiters);

        // when
        final String[] result = numberExtractor.splitText(inputText);

        // then
        assertThat(result).hasSize(1)
                .containsExactly("1234");
    }

}
