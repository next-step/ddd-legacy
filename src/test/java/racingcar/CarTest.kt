package racingcar

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class CarTest : ShouldSpec({
    context("자동차 생성") {
        should("성공") {
            shouldNotThrowAny {
                Car(
                    name = "자동차"
                )
            }
        }

        should("실패 - 자동차 이름이 5 글자를 넘는 경우 예외가 발생한다") {
            // given
            val invalidName = "자동차자동차"

            // when
            val exception = shouldThrowAny {
                Car(
                    name = invalidName
                )
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }
    }

    context("자동차 이동") {
        should("성공 - 숫자가 4 이상인 경우에는 1 만큼 이동한다") {
            // given
            val car1 = Car("자동차1")
            val car1PositionBeforeMove = car1.position

            val car2 = Car("자동차2")
            val car2PositionBeforeMove = car2.position

            // when
            car1.move(4)
            car2.move(5)

            // then
            car1.position shouldBe (car1PositionBeforeMove + 1)
            car2.position shouldBe (car2PositionBeforeMove + 1)
        }

        should("실패 - 숫자가 4 미만인 경우에는 이동하지 않는다") {
            // given
            val car = Car("자동차")
            val carPositionBeforeMove = car.position

            // when
            car.move(3)

            // then
            car.position shouldBe carPositionBeforeMove
        }
    }
})
