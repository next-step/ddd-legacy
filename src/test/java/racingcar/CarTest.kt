package racingcar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CarTest {
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    fun test1() {
        // given
        val name = "람보르으기니"

        // when
        assertThrows(IllegalArgumentException::class.java) {
            Car(name = name)
        }
    }

    @DisplayName("move 메서드에는 0~9 사이의 숫자만 넘길수 있다")
    @Test
    fun test2() {
        // given
        val name = "벤츠"
        val car = Car(name = name)

        // when
        assertThrows(IllegalArgumentException::class.java) {
            car.move(condition = -1)
        }
    }

    @DisplayName("move 메서드에 4 보다 작은 숫자를 넘기면 움직이지 않음")
    @Test
    fun test3() {
        // given
        val name = "벤츠"
        val car = Car(name = name)

        // when
        car.move(condition = 3)

        // then
        assertEquals(0, car.currentPosition)
    }

    @DisplayName("move 메서드에 4 이상의 숫자를 넘기면 움직임")
    @Test
    fun test4() {
        // given
        val name = "벤츠"
        val car = Car(name = name)

        // when
        car.move(4)

        // then
        assertEquals(1, car.currentPosition)
    }
}
