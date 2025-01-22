package stringcalculator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource

class StringCalculatorTest {

    @DisplayName("null or 빈 문자열은 0을 반환한다")
    @NullAndEmptySource
    @ParameterizedTest
    fun nullOrEmptyText(text: String?) {
        val res = StringCalculator(text).sum()
        assertEquals(0, res)
    }

    @DisplayName("숫자 하나를 문자열로 입력했을 경우 해당 숫자를 반환한다")
    @ValueSource(strings = ["0", "99999999"])
    @ParameterizedTest
    fun number(text: String) {
        val res = StringCalculator(text).sum()
        assertEquals(text, res.toString())
    }

    @DisplayName("숫자 두개를 컴마로 구분한 경우 두 숫자의 합을 반환한다")
    @ValueSource(strings = ["1,2"])
    @ParameterizedTest
    fun twoNumbersWithCommaSeparator(text: String) {
        val res = StringCalculator(text).sum()
        assertEquals(res, 3)
    }

    @DisplayName("쉼표 외에 콜론을 구분자로 사용하고 합을 반환한다")
    @ValueSource(strings = ["1,2:3"])
    @ParameterizedTest
    fun threeNumbersWithColonSeparator(text: String) {
        val res = StringCalculator(text).sum()
        assertEquals(res, 6)
    }

    @DisplayName("//와 \n 문자 사이에 커스텀 구분자를 사용할 수 있다")
    @ValueSource(strings = ["//;\n1;2;3", "//;\n1,2;3"])
    @ParameterizedTest
    fun threeNumbersWithCustomSeparator(text: String) {
        val res = StringCalculator(text).sum()
        assertEquals(res, 6)
    }

    @DisplayName("음수를 전달할 경우 RuntimeException 에러가 발생해야 한다")
    @ValueSource(strings = ["-1,2,3"])
    @ParameterizedTest
    fun negative(text: String) {
        assertThrows<RuntimeException> { StringCalculator(text).sum() }
    }
}
