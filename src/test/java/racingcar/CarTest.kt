package racingcar

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.random.Random

class CarTest {

    @Test
    @DisplayName("자동차의 이름이 5글자 초과이면 예외 발생")
    fun constructor() {
        assertThatIllegalArgumentException()
            .isThrownBy { Car("5글자 초과 이름") }
    }

    @Test
    @DisplayName("[Random] 숫자가 4 이상이면 이동")
    fun move() {
        val condition = Random.nextInt(4, 10)
        val car = Car("dawn")
        car.move(condition)
        assertThat(car.position).isEqualTo(1)
    }

    @ParameterizedTest
    @ValueSource(ints = [4, 5, 6, 7, 8, 9])
    @DisplayName("[Parameterized] 숫자가 4 이상이면 이동")
    fun move(condition: Int) {
        val car = Car("dawn")
        car.move(condition)
        assertThat(car.position).isEqualTo(1)
    }

    @Test
    @DisplayName("[Random] 숫자가 4 미만이면 정지")
    fun stop() {
        val condition = Random.nextInt(4)
        val car = Car("dawn")
        car.move(condition)
        assertThat(car.position).isEqualTo(0)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3])
    @DisplayName("[Parameterized] 숫자가 4 미만이면 정지")
    fun stop(condition: Int) {
        val car = Car("dawn")
        car.move(condition)
        assertThat(car.position).isEqualTo(0)
    }
}