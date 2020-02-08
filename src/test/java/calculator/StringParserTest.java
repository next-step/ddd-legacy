package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringParserTest {

    private StringParser stringParser;

    @BeforeEach
    void setUp() {
        stringParser = new StringParser();
    }

    @DisplayName("파싱 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"1:2:3", "1,2,3", "1,2:3", "//;\n1;2;3"})
    void addEmptyText(String input) {
        String[] result = {"1","2","3"};
        assertThat(stringParser.parse(input)).isEqualTo(result);
    }
}
