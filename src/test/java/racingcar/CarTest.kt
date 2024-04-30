package racingcar

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
}
