package calculator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ParsingServiceTest {

    @Test
    fun `기본 구분자와 숫자 파싱`() {
        val input = InputString.of("1,2,3")
        assertThat(input.toNonNegativeNumber()).isEqualTo(
            listOf(
                NonNegativeNumber(1),
                NonNegativeNumber(2),
                NonNegativeNumber(3),
            )
        )
    }

    @Test
    fun `커스텀 구분자와 숫자 파싱`() {
        val input = InputString.of("//;\n1;2;3")
        assertThat(input.toNonNegativeNumber()).isEqualTo(
            listOf(
                NonNegativeNumber(1),
                NonNegativeNumber(2),
                NonNegativeNumber(3),
            )
        )
    }
}
