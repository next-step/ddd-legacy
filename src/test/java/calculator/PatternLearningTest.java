package calculator;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class PatternLearningTest {
    @Test
    void matcher() {
        String text = "//;\n1;2;3";
        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(text);
        matcher.find();
        assertThat(matcher.group(1)).isEqualTo(";");
    }
}
