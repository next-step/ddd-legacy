package calculator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatRuntimeException
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.random.Random

class InputStringTest {

    @Test
    fun `기본 구분자와 숫자가 있는 정상 패턴`() = repeat(100) {
        val target = RandomFixtureGenerator.generateBy(10, useCustomDelimiter = false)
        assertThat(InputString.of(target).src).isEqualTo(target)
    }

    @Test
    fun `커스텀 구분자와 숫자가 있는 정상 패턴`() = repeat(100) {
        val target = RandomFixtureGenerator.generateBy(10, useCustomDelimiter = true)
        assertThat(InputString.of(target).src).isEqualTo(target)
    }

    @ParameterizedTest
    @NullAndEmptySource
    fun `빈 문자열 또는 널을 입력하면 입력값을 0으로 변환`(target: String?) {
        assertThat(InputString.of(target).src).isEqualTo("0")
    }

    @ParameterizedTest
    @ValueSource(strings = ["a", "a,b,c", "a,b:c", "//;\na;b;c"])
    fun `숫자가 아닌 문자를 입력하면 예외 발생`(target: String) {
        assertThatRuntimeException().isThrownBy { InputString.of(target) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["-1", "-1,2,3", "-1,-2,3", "0,-2,3"])
    fun `음수를 입력하면 예외 발생`(target: String) {
        assertThatRuntimeException().isThrownBy { InputString.of(target) }
    }
}

object RandomFixtureGenerator {

    private val chars = ('A'..'Z').toList() + ('a'..'z').toList() + "!@#$%^&*()-_=+[]{}|;:',.<>?/`~".toList()

    fun generateBy(size: Int, useCustomDelimiter: Boolean = false): String {
        val randomNums = List(size) { Random.nextInt(0, 100) }

        if (useCustomDelimiter) {
            val delimiter = chars.random().toString()
            val randomTarget = randomNums.joinToString(delimiter)
            return """//$delimiter\n$randomTarget"""
        }

        val delimiter = listOf(",", ":").random()
        return randomNums.joinToString(delimiter)
    }
}
