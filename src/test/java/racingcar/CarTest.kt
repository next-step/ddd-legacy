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

    @DisplayName("숫자가 4 이상이면 자동차는 움직인다. ")
    @ValueSource(ints = [4, 5, 6, 7, 8, 9])
    @ParameterizedTest
    fun move(condition: Int) {
        val car = Car("hwandoli")
        car.move(condition)
        assertThat(car.position).isEqualTo(1)
    }

    @DisplayName("숫자가 4 미만이면 자동차는 정지한다. ")
    @ValueSource(ints = [0, 1, 2, 3])
    @ParameterizedTest
    fun stop(condition: Int) {
        val car = Car("hwandoli")
        car.move(condition)
        assertThat(car.position).isEqualTo(0)

    }
}
