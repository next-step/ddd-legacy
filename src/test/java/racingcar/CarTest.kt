package racingcar

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CarTest {
    @DisplayName("자동차의 이름은 5글자를 초과하면 안된다.")
    @Test
    fun constructor() {
        assertThatIllegalArgumentException().isThrownBy { Car("안녕") }
    }
    @DisplayName("이동 조건에 부합하면 자동차는 움직인다.")
    @Test
    fun move() {
        val car = Car("hwandoli")
        car.move(ForwardStrategy())
        assertThat(car.position).isEqualTo(1)
    }

    @DisplayName("숫자가 4 미만이면 자동차는 정지한다. ")
    @Test
    fun stop() {
        val car = Car("hwandoli")
        car.move(StopStrategy())
        assertThat(car.position).isEqualTo(0)
    }
}
