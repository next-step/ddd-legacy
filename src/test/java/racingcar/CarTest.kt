package racingcar

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CarTest {

    @Test
    @DisplayName("자동차의 이름이 5글자 초과이면 예외 발생")
    fun constructor() {
        assertThatIllegalArgumentException()
            .isThrownBy { Car("5글자 초과 이름") }
    }

    @Test
    @DisplayName("자동차는 움직인다")
    fun move() {
        val car = Car("dawn")
        car.move(ForwardStrategy())
        assertThat(car.position).isEqualTo(1)
    }

    @Test
    @DisplayName("자동차는 정지한다")
    fun stop() {
        val car = Car("dawn")
        car.move(StopStrategy())
        assertThat(car.position).isEqualTo(0)
    }
}