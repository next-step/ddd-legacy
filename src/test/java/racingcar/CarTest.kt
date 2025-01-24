package racingcar

import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CarTest {
    @DisplayName("자동차의 이름은 5글자를 초과하면 안된다.")
    @Test
    fun constructor() {
        assertThatIllegalArgumentException().isThrownBy { Car("안녕") }
    }
}
