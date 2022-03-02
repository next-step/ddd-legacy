package caculator;

import static org.assertj.core.api.Assertions.assertThat;

import caculator.domain.Numbers;
import caculator.domain.StringSplitter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringSplitterTest {

    @DisplayName("기본 구분자로 문자 분리")
    @ParameterizedTest(name = "[{arguments}]")
    @ValueSource(strings = {
        "1,2,3",
        "1,2:3"
    })
    void split(String stringNumbers) {
        //when
        Numbers numbers = StringSplitter.split(stringNumbers);

        //then
        assertThat(numbers).isEqualTo(Numbers.from(new String[]{"1", "2", "3"}));

    }

}
