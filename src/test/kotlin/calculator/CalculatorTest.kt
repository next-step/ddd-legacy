package calculator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatRuntimeException
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource

class CalculatorTest {

    @ParameterizedTest
    @CsvSource("0,1,2|3", "1,2,3|6", delimiter = '|')
    fun `기본 구분자와 숫자 계산 성공`(target: String, expected: Int) {
        assertThat(SUT.calculate(target)).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource("""//;\n0;2;3|5""", """//;\n1;0;3|4""", """//.\n1.0.3|4""", delimiter = '|')
    fun `커스텀 구분자와 숫자 계산 성공`(target: String, expected: Int) {
        assertThat(SUT.calculate(target)).isEqualTo(expected)
    }

    @ParameterizedTest
    @ValueSource(strings = ["""//;\n0;2,3""", """//;\n1:0;3"""])
    fun `커스텀 구분자와 기본 구분자를 섞어 사용할 수 없음`(target: String) {
        assertThatRuntimeException().isThrownBy { SUT.calculate(target) }
    }

    @ParameterizedTest
    @NullAndEmptySource
    fun `빈 문자열 또는 널`(target: String?) {
        assertThat(SUT.calculate(target)).isEqualTo(0)
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
