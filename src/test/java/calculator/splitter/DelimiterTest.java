package calculator.splitter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Delimiter")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DelimiterTest {

    private Delimiter delimiter;

    @BeforeEach
    void setUp() {
        delimiter = new Delimiter(",");
    }

    @Test
    void 구분자를_추가한다() {
        delimiter.addDelimiter(";");
        assertThat(delimiter).isEqualTo(new Delimiter(",|;"));
    }

    @Test
    void 문자열을_분리한다() {
        SplitTargetText splitTargetText = new SplitTargetText("가,나,다");
        assertThat(delimiter.split(splitTargetText)).contains("가", "나", "다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"가,나,다", "가,나;다", "가;나;다"})
    void 구분자를_추가한후_문자열을_분리한다(final String text) {
        SplitTargetText splitTargetText = new SplitTargetText(text);

        delimiter.addDelimiter(";");

        assertThat(delimiter.split(splitTargetText)).contains("가", "나", "다");
    }
}