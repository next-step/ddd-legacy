package stringcaculator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class CalculatorTest {
    val calculator = Calculator()


    @DisplayName("빈 문자열을 입력한 경우 0을 반환한다.")
    @Test
    fun 빈문자열_입력() {
        val result = calculator.add("")
        assertThat(result).isEqualTo(0)
    }

    @DisplayName("null을 입력한 경우 0을 반환한다.")
    @Test
    fun null_입력() {
        val result = calculator.add(null)
        assertThat(result).isEqualTo(0)
    }

    @DisplayName("숫자 하나를 입력한 경우 해당 숫자를 반환한다.")
    @Test
    fun 숫자하나입력() {
        val result = calculator.add("7")
        assertThat(result).isEqualTo(7)
    }

    @DisplayName("숫자 사이에 컴마 또는 콜론 구분자 입력할 경우 숫자의 합을 반환한다.")
    @Test
    fun 문자열을_컴마_또는_콜론으로_구분하여_합을_반환() {
        val result = calculator.add("1,2:3,4")
        assertThat(result).isEqualTo(10)
    }

    @DisplayName("커스텀 문자 구문자를 사용할 수 있다.")
    @Test
    fun 문자열에서_커스텀_구분자를_지정하여_합을_반환() {
        val result = calculator.add("//;\n1;2;3")
        assertThat(result).isEqualTo(6)
    }

    @DisplayName("문자열에 음수가 전달되면 RuntimeException을 throw 한다.")
    @Test
    fun 문자열에_음수_전달시_RuntimeException_throw() {
        assertThatExceptionOfType(RuntimeException::class.java)
            .isThrownBy { calculator.add("-1") }
    }

    @DisplayName("문자열에 음수가 전달되면 RuntimeException을 throw 한다.")
    @Test
    fun 문자열에_숫자이외의값_전달시_RuntimeException_throw() {
        assertThatExceptionOfType(RuntimeException::class.java)
            .isThrownBy { calculator.add("1:232:$:3") }
    }
}