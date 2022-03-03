package caculator;

import static org.assertj.core.api.Assertions.assertThat;

import caculator.domain.Numbers;
import caculator.domain.StringNet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringNetTest {

    @DisplayName("기본 구분자로 문자 분리")
    @ParameterizedTest(name = "[{arguments}]")
    @ValueSource(strings = {
        "1,2,3",
        "1,2:3"
    })
    void split(String stringNumbers) {
        //when
        Numbers numbers = StringNet.split(stringNumbers);

        //then
        assertThat(numbers).isEqualTo(Numbers.from(new String[]{"1", "2", "3"}));

    }

    @DisplayName("커스텀 구분자로 분리")
    @ParameterizedTest(name = "[{arguments}]")
    @ValueSource(strings = {
        "//;\n1;2;3",
        "//!\n1!2!3",
    })
    void customDelimiter(String stringNumbers) {
        //when
        Numbers numbers = StringNet.split(stringNumbers);

        //then
        assertThat(numbers).isEqualTo(Numbers.from(new String[]{"1", "2", "3"}));

    }

    @DisplayName("빈 문자열 또는 null")
    @ParameterizedTest(name = "[{arguments}]")
    @NullAndEmptySource
    void emptyOrNull(String nullOrEmpty) {
        //when
        Numbers actual = StringNet.split(nullOrEmpty);

        //then
        assertThat(actual).isEqualTo(Numbers.EMPTY);

    }
}
