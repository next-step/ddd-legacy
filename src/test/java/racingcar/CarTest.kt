package racingcar

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
}
