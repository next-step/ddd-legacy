package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;

class CalculatorNumberTest {

    @Test
    @DisplayName(value = "원시값 객체 정상 생성 확인")
    void success() {
        CalculatorNumber number = CalculatorNumber.from("1");
        assertThat(number.getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName(value = "음수 값을 넘기는 경우 RuntimeException Throw")
    void failTest1() {
        assertThatRuntimeException().isThrownBy(() -> CalculatorNumber.from("-1"));
    }

    @Test
    @DisplayName(value = "숫자가 아닌 형태 값을 넘기는 경우 RuntimeException Throw")
    void failTest2() {
        assertThatRuntimeException().isThrownBy(() -> CalculatorNumber.from("a"));
    }

    @Test
    @DisplayName(value = "동일한 숫자가 캐시에 있는 경우 캐시데이터를 반환한다.")
    void cache() {
        CalculatorNumber number1 = CalculatorNumber.from("1");
        CalculatorNumber number2 = CalculatorNumber.from("1");
        assertThat(number1).isEqualTo(number2);
    }
    @Test
    @DisplayName(value = "동일한 숫자인 경우 동일 객체로 판단한다.")
    void VO() {
        CalculatorNumber number1 = CalculatorNumber.from("1");
        CalculatorNumber number2 = CalculatorNumber.from("1");
        assertThat(number1).isEqualTo(number2);
    }
}
