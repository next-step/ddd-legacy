package stringcalculator;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class StringCalculatorTest {
    @DisplayName("쉼표 또는 콜론을 구분자로 이루어진 숫자를 입력하면, 숫자들의 총합을 반환한다.")
    void successTest() {

    }

    @DisplayName("//와 \\n 사이에 위치한 문자를 문자열에 포함시, 이를 커스텀 구분자로 사용, 숫자들의 총합을 반환한다.")
    void customDelimiterTest() {

    }

    @DisplayName("문자열 계산기에 숫자 이외의 값 혹은 음수를 입력시 RuntimeException 발생한다.")
    void invalidInputExceptionTest() {

    }
}