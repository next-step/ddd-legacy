package stringcalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NumberParserTest {

    private final NumberParser numberParser = new NumberParser();

    @ParameterizedTest(name = ", 또는 : 구분자로 숫자를 분리할 수 있다. {0}인 경우")
    @ValueSource(strings = {"1,2,3", "1:2:3", "1,2:3"})
    void defaultDelimiter(String value) {
        assertThat(numberParser.parse(value)).isEqualTo(Arrays.asList(1, 2, 3));
    }

    @ParameterizedTest(name = ", 또는 : 구분자 이외의 문자열인 경우 예외가 발생한다. {0}인 경우")
    @ValueSource(strings = {"1!2!3", "1@2@3", "1!2@3"})
    void notParse(String value) {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> numberParser.parse(value));
    }

    @ParameterizedTest(name = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다. {0}인 경우")
    @ValueSource(strings = {"//!\n1!2!3", "//@\n1@2@3", "//!\n//@\n1!2@3"})
    void customDelimiter(String value) {
        assertThat(numberParser.parse(value)).isEqualTo(Arrays.asList(1, 2, 3));
    }

    @ParameterizedTest(name = "null 또는 빈 문자열이라면 비어있는 리스트를 반환한다. {0}인 경우")
    @NullAndEmptySource
    void nullOrEmpty(String value) {
        assertThat(numberParser.parse(value))
            .isEqualTo(new ArrayList<>())
            .isEmpty();
    }
}
