package caculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import caculator.domain.StringNumber;
import caculator.domain.StringNumberException;
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
    
    @DisplayName("숫자 이외의 값 예외")
    @Test
    void notANumberException() {

        String 숫자_아닌_문자 = "일";

        assertThatThrownBy(() -> StringNumber.valueOf(숫자_아닌_문자))
            .isInstanceOf(StringNumberException.class)
            .hasMessageEndingWith(숫자_아닌_문자);
    }
}
