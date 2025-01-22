package stringcalculator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
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
    fun number(text: String?) {
        val res = StringCalculator(text).sum()
        assertEquals(text, res.toString())
    }
}
