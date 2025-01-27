package stringcalculator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource

internal class StringCalculatorTest {

    private val sut = StringCalculator()

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    fun emptyOrNull(text: String?) {
        assertThat(sut.calculate(text)).isZero
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = ["1"])
    fun oneNumber(text: String) {
        assertThat(sut.calculate(text)).isSameAs(text.toInt())
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = ["1,2"])
    fun twoNumbers(text: String?) {
        assertThat(sut.calculate(text)).isSameAs(3)
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = ["1,2:3"])
    fun colons(text: String?) {
        assertThat(sut.calculate(text)).isSameAs(6)
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = ["//;\n1;2;3"])
    fun customDelimiter(text: String?) {
        assertThat(sut.calculate(text)).isSameAs(6)
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = ["-1", "1:-3", "3,5,7:-29"])
    fun negative(text: String) {
        assertThatExceptionOfType(RuntimeException::class.java).isThrownBy {
            sut.calculate(text)
        }
    }

    @DisplayName(value = "문자열 계산기로 합을 계산한다")
    @ParameterizedTest
    @MethodSource("provideSumArguments")
    fun sum(text: String, result: Int) {
        assertThat(sut.calculate(text)).isSameAs(result)
    }

    companion object {
        @JvmStatic
        fun provideSumArguments() = listOf(
            Arguments.of("0", 0),
            Arguments.of("1", 1),
            Arguments.of("1,3,5", 9),
            Arguments.of("4:15,25", 44),
            Arguments.of("//;\n1;2;3,4:5", 15),
        )
    }
}
