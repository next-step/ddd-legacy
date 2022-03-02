package caculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import caculator.domain.StringNumber;
import caculator.domain.StringNumberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringNumberTest {

    @DisplayName("문자타입의 숫자 객체 생성")
    @Test
    void constructor() {
        //given
        String number = "1";

        //when
        StringNumber actual = StringNumber.valueOf(number);

        //then
        assertThat(actual).isEqualTo(StringNumber.valueOf(number));
    }
    
    @DisplayName("숫자 이외 또는 음수")
    @ParameterizedTest(name = "[{arguments}]")
    @ValueSource(strings = "일, -1")
    void notANumberException(String notANumber) {

        assertThatThrownBy(() -> StringNumber.valueOf(notANumber))
            .isInstanceOf(StringNumberException.class)
            .hasMessageEndingWith(notANumber);
    }

}
