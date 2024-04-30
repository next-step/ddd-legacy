package racingcar

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

class RacingCarTest : ExpectSpec({
    expect("자동차 이름이 5글자를 넘으면 예외가 발생한다") {
        shouldThrow<IllegalArgumentException> {
            Car("123456")
        }
    }

    expect("숫자가 4 이상인 경우 자동차는 전진한다") {
        val car = Car("car")
        car.move(MovingStrategy.Forward)
        car.position shouldBe 1
    }

    expect("숫자가 4 미만인 경우 자동차는 멈춘다.") {
        val car = Car("car")
        car.move(MovingStrategy.Stop)
        car.position shouldBe 0
    }
})
