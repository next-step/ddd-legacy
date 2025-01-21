package racingcar

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CarTest {

    @DisplayName("이름은 5글자를 넘을 수 없다")
    @Test
    fun nameLengthTest() {
        assertThrows<IllegalArgumentException>{
            Car("123456")
        }
    }
}