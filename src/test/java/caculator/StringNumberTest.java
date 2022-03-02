package caculator;

import static org.assertj.core.api.Assertions.assertThat;

import caculator.domain.StringNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    
}
