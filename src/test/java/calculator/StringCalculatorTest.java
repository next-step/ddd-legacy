package calculator;


/*

문자열 계산기에 문자열을 입력하고
구분자를 기준으로 분리한 각 숫자의 합을 반환한다.

문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.

1. 기본 구분자 : 쉼표(,) 또는 콜론(:)
2. 커스텀 구분자 : 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자

공백 문자열을 입력할 경우  : “” => 0
문자를 입력할 경우 :
음수를 입력할 경우 :
기본 구분자 사용 : "1,2" => 3,
기본 구분자 사용 : "1,2,3" => 6
기본 구분자 쉼표와 콜론 동시 사용 : “1,2:3” => 6
커스텀 구분자 사용 : “//;\n1;2;3” => 6
*/

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static calculator.StringCalculator.of;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("문자열 계산기")
public class StringCalculatorTest {

    @DisplayName("공백 문자열을 입력할 경우 “” => 0 ")
    @Test
    void name() {
        String value = "";
        assertThat(of(value).calculate()).isEqualTo(0);
    }

    @DisplayName("기본 구분자 사용 : /* 1,2 => 3 */")
    @Test
    void defaultCalculate() {

        //        given
        String value = "1,2";

        //        then
        assertThat(of(value).calculate()).isEqualTo(3);

    }

    @DisplayName("기본 구분자 사용 : \"1,2,3\" => 6")
    @Test
    void 세개숫자의합() {

        //        given
        String value = "1,2,3";

        //        then
        assertThat(of(value).calculate()).isEqualTo(6);

    }

}
