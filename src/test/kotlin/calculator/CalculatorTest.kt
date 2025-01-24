package calculator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatRuntimeException
import org.junit.jupiter.api.Test

class CalculatorTest {

    @Test
    fun `기본 구분자와 숫자`() {
        val target = "1,2,3"
        assertThat(SUT.calculate(target)).isEqualTo(6)
    }

    @Test
    fun `커스텀 구분자와 숫자`() {
        val target = "//;\n1;2;3"
        assertThat(SUT.calculate(target)).isEqualTo(6)
    }

    @Test
    fun `빈 문자열 또는 널`() {
        val emptyString = ""
        assertThat(SUT.calculate(emptyString)).isEqualTo(0)

        val nullable = null
        assertThat(SUT.calculate(nullable)).isEqualTo(0)
    }

    @Test
    fun `기본 구분자와 숫자가 아닌 문자`() {
        val target = "a,b,c"
        assertThatRuntimeException().isThrownBy { SUT.calculate(target) }
    }

    @Test
    fun `기본 구분자와 음수`() {
        val target = "-1,2,3"
        assertThatRuntimeException().isThrownBy { InputString.of(target) }
    }

    companion object {
        private val SUT = Calculator()
    }
}