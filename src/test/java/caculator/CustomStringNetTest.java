package caculator;

import static org.assertj.core.api.Assertions.assertThat;

import caculator.domain.CustomStringNet;
import caculator.domain.StringNet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CustomStringNetTest {

    @DisplayName("커스텀 구분자로 분리")
    @ParameterizedTest(name = "[{arguments}]")
    @ValueSource(strings = {
        "//;\n1;2;3",
        "//!\n1!2!3"
    })
    void strain(String stringNumbers) {
        //given
        StringNet customStringNet = new CustomStringNet();

        //when
        String[] actual = customStringNet.strain(stringNumbers);

        //then
        assertThat(actual).isEqualTo(new String[]{"1", "2", "3"});

    }

}
