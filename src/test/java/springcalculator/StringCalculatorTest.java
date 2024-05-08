package springcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class StringCalculatorTest {

    @DisplayName("빈 문자열 또는 null 값 시 0값을 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyString(String input) {
        // when then
        assertThat(new StringCalculator(input).add()).isZero();
    }

    @DisplayName("구분자가 없는 숫자 문자열 입력 시 해당 숫자를 반환한다.")
    @Test
    void singleNumber() {
        // given
        String input = "1";

        // when then
        assertThat(new StringCalculator(input).add()).isEqualTo(Integer.parseInt(input));
    }


    @DisplayName("쉼표(,)로 구분 된 문자열 숫자 값의 합산을 반환한다.")
    @Test
    void 쉼표_String() {
        // given
        String input = "1,2";

        // when then
        assertThat(new StringCalculator(input).add()).isEqualTo(3);
    }


    @DisplayName("콜론(:)로 구분 된 문자열 숫자 값의 합산을 반환한다.")
    @Test
    void 콜론_String() {
        // given
        String input = "1,2,3";

        // when then
        assertThat(new StringCalculator(input).add()).isEqualTo(6);
    }

    @DisplayName("쉼표와 콜론이 한 문자열에 함계 사용될 수 있다.")
    @Test
    void 쉼표_콜론_String() {
        // given
        String input = "1,2:3";

        // when then
        assertThat(new StringCalculator(input).add()).isEqualTo(6);
    }

}
