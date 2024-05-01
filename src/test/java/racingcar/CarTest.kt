package racingcar

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CarTest {

    @DisplayName("자동차 이름은 5글자를 초과할 수 없다.")
    @Test
    fun constructor() {
        assertThatIllegalArgumentException().isThrownBy { Car.init(name = "자동차이름은") }
    }

    @DisplayName("숫자가 4 이상인 경우에 자동차가 전진한다.")
    @Test
    fun move() {
        val car = Car.init(name = "자동차")
        car.move(GoStrategy())
        assertThat(car.position).isEqualTo(1)
    }

    @DisplayName("숫자가 4 미만인 경우에 자동차가 전진하지 않는다.")
    @Test
    fun notMove() {
        val car = Car.init(name = "자동차")
        car.move(StopStrategy())
        assertThat(car.position).isEqualTo(0)
    }
}