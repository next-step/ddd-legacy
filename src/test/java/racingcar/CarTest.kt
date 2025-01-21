package racingcar

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CarTest {

    @Test
    @DisplayName("자동차의 이름이 5글자 초과이면 예외 발생")
    fun constructor() {
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy { Car("5글자 초과 이름") }
    }
}