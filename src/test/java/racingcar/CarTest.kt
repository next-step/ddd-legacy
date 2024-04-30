package racingcar

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CarTest {
    @DisplayName("자동차 이름은 5를 넘을 수 없다")
    @Test
    fun carNameLengthLimit() {
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy { Car("123456") }
    }

    @DisplayName("숫자가 4이상일 때 자동차의 위치가 증가한다")
    @Test
    fun carMoveTest() {
        val car = Car("car")

        car.move(4)

        Assertions.assertThat(car.position).isEqualTo(1)
    }

    @DisplayName("숫자가 4미만일 때 자동차의 위치는 그대로 0")
    @Test
    fun carStopTest() {
        val car = Car("car")

        car.move(2)

        Assertions.assertThat(car.position).isEqualTo(0)
    }
}