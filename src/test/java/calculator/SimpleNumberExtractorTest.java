package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleNumberExtractorTest {

    private SimpleNumberExtractor numberExtractor;


    @BeforeEach
    void setup() {
        numberExtractor = new SimpleNumberExtractor();
    }


    @Test
    @DisplayName("주어진 문자열과 구분자로 문자열을 분리한다.")
    void testSplitNumbers() {
        // given
        final String text = "1,2:3";
        final String delimiters = ",:";

        // when
        final String[] result = numberExtractor.extractNumber(text, delimiters);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("1", "2", "3");
    }

}
