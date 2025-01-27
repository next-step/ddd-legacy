package stringcaculator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CalculatorTest {

    @DisplayName("빈 문자열을 입력한 경우 0을 반환한다.")
    @Test
    fun 빈문자열_입력() {
        val calculator = Calculator()
        val result = calculator.add("")
        assertThat(result).isEqualTo(0)
    }

    @DisplayName("null을 입력한 경우 0을 반환한다.")
    @Test
    fun null_입력() {
        val calculator = Calculator()
        val result = calculator.add(null)
        assertThat(result).isEqualTo(0)
    }

}