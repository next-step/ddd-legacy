package racingcar

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CarTest {

    @DisplayName("이름은 5글자를 넘을 수 없다")
    @Test
    fun nameLengthTest() {
        assertThrows<IllegalArgumentException> {
            Car("123456")
        }
    }

    @DisplayName("자동차가 움직인다")
    @Test
    fun move() {
        val c = Car("car")
        c.move(ForwardStrategy())

        Assertions.assertEquals(c.position, 1)
    }

    @DisplayName("자동차가 멈춘다")
    @Test
    fun stop() {
        val c = Car("car")
        c.move(StopStrategy())

        Assertions.assertEquals(c.position, 0)
    }

    @DisplayName("무작위 값이 4보다 크면 움직인다")
    @ValueSource(ints = [5, 6, 7, 8])
    @ParameterizedTest
    fun conditionalMove(condition: Int) {
        val c = Car("car")
        c.move(condition)

        Assertions.assertEquals(c.position, 1)
    }

    @DisplayName("무작위 값이 4보다 4보다 작거나 같으면 움직인다")
    @ValueSource(ints = [1, 2, 3, 4])
    @ParameterizedTest
    fun conditionalStop(condition: Int) {
        val c = Car("car")
        c.move(condition)

        Assertions.assertEquals(c.position, 0)
    }
}
