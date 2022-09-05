package string_calculator.string_parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringParserFactoryTest {

    private final StringParserFactory stringParserFactory = new StringParserFactory();

    @DisplayName("string이 null 또는 empty인 경우 SimpleStringParser를 만들어야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void string_null_or_empty(final String string) {
        assertThat(this.stringParserFactory.createStringParser(string))
                .isInstanceOf(SimpleStringParser.class);
    }

    @DisplayName("string이 blank인 경우 SimpleStringParser를 만들어야 한다.")
    @ValueSource(strings = {" ", "\t"})
    @ParameterizedTest
    void string_blank(final String string) {
        assertThat(this.stringParserFactory.createStringParser(string))
                .isInstanceOf(SimpleStringParser.class);
    }

    @DisplayName("string이 CustomDelimiterStringParser의 pattern과 일치하는 경우 CustomDelimiterStringParser를 만들어야 한다.")
    @ValueSource(strings = {
            "//!\n1!2",
            "//@\n1@2",
            "//#\n1#2",
            "//$\n1$2",
            "//%\n1%2",
    })
    @ParameterizedTest
    void string_custom_delimiter_pattern(final String string) {
        assertThat(this.stringParserFactory.createStringParser(string))
                .isInstanceOf(CustomDelimiterStringParser.class);
    }

    @DisplayName("그냥 문자열인 경우 SimpleStringParser를 만들어야 한다.")
    @ValueSource(strings = {
            "1,2,3,4,5",
            "1:2:3:4:5",
            "1,2:3,4:5",
    })
    @ParameterizedTest
    void string_simple(final String string) {
        assertThat(this.stringParserFactory.createStringParser(string))
                .isInstanceOf(SimpleStringParser.class);
    }
}
