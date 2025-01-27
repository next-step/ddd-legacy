package stringcalculator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import stringcalculator.ExpressionMatcher.Companion.Expression

internal class ExpressionMatcherTest {

    private val sut = ExpressionMatcher()

    @DisplayName("커스텀 구분자가 없을경우 기본 구분자로 쉼표(,)와 콜론(:)을 포함시킨다")
    @Test
    fun useDefaultDelimiterIfNoCustomDelimiter() {
        // given
        val expressionInput = "1:2,3"

        // when
        val actual = sut.transform(expressionInput)

        // then
        assertThat(actual).isEqualTo(Expression(DEFAULT_DELIMITERS, expressionInput))
    }

    @DisplayName("커스텀 구분자는 //와 \n 문자 사이에 지정해 Expression에 포함할 수 있다")
    @Test
    fun useCustomDelimiterIfHaveCustomDelimiterInput() {
        // given
        val expressionInput = "//;\n1:2,3;4"

        // when
        val actual = sut.transform(expressionInput)

        // then
        assertThat(actual).isEqualTo(Expression(listOf(*DEFAULT_DELIMITERS.toTypedArray(), ";"), "1:2,3;4"))
    }

    @DisplayName("커스텀 구분자 이외의 모든 문자열을 입력으로 변환한다")
    @Test
    fun useAllInputIfNoDelimiter() {
        // given
        val expressionInput = ",a;2:3,12"

        // when
        val actual = sut.transform(expressionInput)

        // then
        assertThat(actual).isEqualTo(Expression(DEFAULT_DELIMITERS, ",a;2:3,12"))
    }

    companion object {
        private val DEFAULT_DELIMITERS = listOf(",", ":")
    }
}