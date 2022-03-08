package caculator;

import static org.assertj.core.api.Assertions.assertThat;

import caculator.domain.DefaultStringNet;
import caculator.domain.StringNet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultStringNetTest {

    @DisplayName("기본 구분자로 문자 분리")
    @ParameterizedTest(name = "[{arguments}]")
    @ValueSource(strings = {
        "1,2,3",
        "1,2:3"
    })
    void strain(String stringNumbers) {
        //given
        StringNet defaultStringNet = new DefaultStringNet();

        //when
        String[] actual = defaultStringNet.strain(stringNumbers);

        //then
        assertThat(actual).isEqualTo(new String[]{"1", "2", "3"});
    }
}
