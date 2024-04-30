package racingcar

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CarTest {
    @DisplayName("자동차 이름은 5글자가 넘을 수 없다")
    @Test
    fun test1() {
        assertThatIllegalArgumentException().isThrownBy {
            Car(name = "동해물과백두산이")
        }
    }

    @DisplayName("숫자가 4 이상인 경우 자동차는 전진한다")
    @Test
    fun test2() {
        val car = Car(name = "홍길동")
        car.move(GoStrategy())
        assertThat(car.position).isEqualTo(1)
    }

    @DisplayName("숫자가 4 미만인 경우 자동차는 움직이지 않는다")
    @Test
    fun test3() {
        val car = Car(name = "홍길동")
        car.move(StopStrategy())
        assertThat(car.position).isEqualTo(0)
    }
}