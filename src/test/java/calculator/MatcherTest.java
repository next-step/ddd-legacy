package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class MatcherTest {

    @DisplayName(value = "matcher 동작 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        Pattern pattern = Pattern.compile("//(.*?)\\n");
        Matcher matcher = pattern.matcher(text);

        if (matcher.matches()) {
            String matched = matcher.group(0);
            assertThat(matched).isEqualTo("//;\n");

            String extraSeparator = matcher.group(1);
            assertThat(extraSeparator).isEqualTo(";");

            int matchedLength = matcher.end();
            String remains = text.substring(matchedLength);
            assertThat(remains).isEqualTo("[1, 2, 3]");
        }
    }
}
