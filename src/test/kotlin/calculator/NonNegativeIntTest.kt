package calculator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatRuntimeException
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class NonNegativeIntTest {

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    fun `0 또는 양수로 생성 성공`(num: Int) {
        val created = NonNegativeInt(num)
        assertThat(created.value).isEqualTo(num)
    }

    @Test
    fun `음수로 생성 불가`() {
        assertThatRuntimeException().isThrownBy { NonNegativeInt(-1) }
    }
}
