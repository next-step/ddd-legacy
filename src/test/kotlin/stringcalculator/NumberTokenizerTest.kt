package stringcalculator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import stringcalculator.ExpressionMatcher.Companion.Expression

internal class NumberTokenizerTest {

    private val sut = NumberTokenizer()

    @DisplayName("숫자로 변환할 수 없을경우 NumberFormatException 예외가 발생한다")
    @Test
    fun errorIfNoNumberFormat() {
        // given
        val delimiters = listOf(":")
        val input = "1:asdf"
        val expr = Expression(delimiters, input)

        // when
        val actual = kotlin.runCatching { sut.tokenize(expr) }

        // then
        assertThat(actual.exceptionOrNull()).isInstanceOf(NumberFormatException::class.java)
    }

    @DisplayName("구분자가 없을경우 입력된 문자열 자체를 숫자로 변환한다")
    @Test
    fun parseNumberIfNoDelimiter() {
        // given
        val delimiters = emptyList<String>()
        val input = "12345"

        // when
        val actual = sut.tokenize(Expression(delimiters, input))

        // then
        assertThat(actual).isEqualTo(listOf(12345))
    }

    @DisplayName("입력된 문자열을 구분자를 통해 구분한다")
    @ParameterizedTest
    @MethodSource("provideDelimiterSource")
    fun tokenize(delimiters: List<String>, input: String, answer: List<Int>) {
        // given
        val exp = Expression(delimiters, input)

        // when
        val actual = sut.tokenize(exp)

        // then
        assertThat(actual).isEqualTo(answer)
    }

    companion object {
        @JvmStatic
        fun provideDelimiterSource() = listOf(
            Arguments.of(listOf(":"), "1:2:3", listOf(1, 2, 3)),
            Arguments.of(listOf(":", ","), "1:2,3", listOf(1, 2, 3)),
            Arguments.of(listOf("=", ",", ":"), "1=2,3:4", listOf(1, 2, 3, 4)),
        )
    }
}