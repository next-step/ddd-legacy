package calculator

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NumberTokenTest {
    @DisplayName("음수의 number token 생성시 RuntimeException 예외 발생")
    @Test
    fun numberTokenRangeTest() {
        Assertions.assertThatExceptionOfType(RuntimeException::class.java)
            .isThrownBy {
                NumberToken(-3)
            }
    }

    @DisplayName("number token의 덧셈 연산은 이들의 합을 가진 새로운 number token")
    @Test
    fun numberTokenPlusTest() {
        val numberToken1 = NumberToken(1)
        val numberToken2 = NumberToken(7)
        val result = numberToken1 + numberToken2

        Assertions.assertThat(result).isEqualTo(NumberToken(8))
    }

    @DisplayName("number tokens의 계산 값은 보유한 number token value의 합")
    @Test
    fun numberTokensCalculateTest() {
        val numberTokens = NumberTokens(listOf(NumberToken(3), NumberToken(5), NumberToken(7)))

        Assertions.assertThat(numberTokens.calculate()).isEqualTo(NumberToken(15))
    }
}
