package calculator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource

class StringCalculatorTest {
    private lateinit var calculator: StringCalculator

    @BeforeEach
    fun setUp() {
        calculator = StringCalculator()
    }

    @DisplayName("빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    fun test1(text: String?) {
        assertThat(calculator.add(text)).isZero()
    }
}
