package calculator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatRuntimeException
import org.junit.jupiter.api.Test
import kotlin.random.Random

class InputStringTest {

    @Test
    fun `기본 구분자와 숫자`() {
        val testString = "${Random.nextInt(0, 20)},${Random.nextInt(0, 20)},${Random.nextInt(0, 20)}"
        assertThat(InputString.of(testString).src).isEqualTo(testString)
    }

    @Test
    fun `커스텀 구분자와 숫자`() {
        val testString = "//;\n1;2;3"
        assertThat(InputString.of(testString).src).isEqualTo(testString)
    }

    @Test
    fun `빈 문자열 또는 널`() {
        val emptySpace = ""
        assertThat(InputString.of(emptySpace).src).isEqualTo("0")

        val nullable = null
        assertThat(InputString.of(nullable).src).isEqualTo("0")
    }

    @Test
    fun `기본 구분자와 숫자가 아닌 문자`() {
        val testString = "a,b,c"
        assertThatRuntimeException().isThrownBy { InputString.of(testString) }
    }

    @Test
    fun `기본 구분자와 음수`() {
        val testString = "-1,2,3"
        assertThatRuntimeException().isThrownBy { InputString.of(testString) }
    }
}