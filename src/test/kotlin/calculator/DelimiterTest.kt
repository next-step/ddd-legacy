package calculator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DelimiterTest {

    @Test
    fun `기본 구분자로 생성`() {
        val testInput = "1,2,3"
        assertThat(Delimiter(testInput).value.toString()).isEqualTo("[,:]")
    }

    @Test
    fun `커스텀 구분자로 생성`() {
        val testInput = "//;\n1,2,3"
        assertThat(Delimiter(testInput).value.toString()).isEqualTo(";")
    }
}