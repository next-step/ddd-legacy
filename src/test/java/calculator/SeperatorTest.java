package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SeperatorTest {

    @DisplayName("구분자는 컴마(,)와 콜론(:)을 사용할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3", "1,2", "1:3", "1:3,2"})
    void defaultDelimiter(final String text) {
        final Seperator seperator = Seperator.of(text);
        assertThat(seperator.getTargetNumber()).isEqualTo(text);
        assertThat(seperator.getDelimiter()).isEqualTo(Seperator.DEFAULT_DELIMITER);
    }

    @DisplayName("//와 \\n문자 사이에 커스텀 구분자를 지정할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1,2:3;4", "//p\n1,2:3p4p5"})
    void customDelimiter(final String text) {
        //given
        final Matcher matcher = Seperator.pattern.matcher(text);
        matcher.find();
        final String customDelimiter = matcher.group(Seperator.ONE);
        final String targetNumber = matcher.group(Seperator.TWO);

        //when
        final Seperator seperator = Seperator.of(text);

        //then
        assertThat(seperator.getTargetNumber()).isEqualTo(targetNumber);
        assertThat(seperator.getDelimiter()).isEqualTo(Seperator.DEFAULT_DELIMITER + Seperator.SEPARATOR + customDelimiter);
    }

}
