package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class DelimiterParserTest {

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text){
        DelimiterParser delimiterParser = new DelimiterParser(text);
        assertThat(delimiterParser.getParsedNumber()).containsExactly("1");
    }

    @DisplayName(" 숫자 두개를 쉼표(,) 구분자로 입력할 경우 delimiter를 제외한 숫자를 반환")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text){
        DelimiterParser delimiterParser = new DelimiterParser(text);
        assertThat(delimiterParser.getParsedNumber()).containsExactly("1", "2");
    }

    @DisplayName("구분자를 쉼표(,) 이외에 콜론(:)을 입력할 경우 delimiter를 제외한 숫자 반환")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text){
        DelimiterParser delimiterParser = new DelimiterParser(text);
        assertThat(delimiterParser.getParsedNumber()).containsExactly("1", "2", "3");
    }

    @DisplayName("//와 \n 문자 사이에 커스텀 구분자를 파싱 후 delimiter를 제외한 숫자 반환")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text){
        DelimiterParser delimiterParser = new DelimiterParser(text);
        assertThat(delimiterParser.getParsedNumber()).containsExactly("1", "2", "3");
    }

}
