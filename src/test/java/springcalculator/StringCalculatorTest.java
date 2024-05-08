package springcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(String input) {
        // when then
        assertThat(new StringCalculator(input).add()).isSameAs(6);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        // given
        String input = "1,-1,2";

        // when then
        assertThatThrownBy(() -> new StringCalculator(input).add())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("음수값을 넣을 수 없습니다.");
    }

    @DisplayName(value = "문자열 계산기에 숫자 이외의 값 전달 시 RuntimeException 예외 처리를 한다.")
    @Test
    void not_numbers() {
        // given
        String input = "1,o,2";

        // when then
        assertThatThrownBy(() -> new StringCalculator(input).add())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("숫자 이외의 값을 들어갈 수 없습니다.");
    }
}
