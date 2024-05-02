package racingcar

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import racingcar.strategy.GoStrategy
import racingcar.strategy.StopStrategy

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
        should("성공 - 이동할 수 있는 조건인 경우 1 만큼 이동한다") {
            // given
            val car = Car("자동차")
            val carPositionBeforeMove = car.position
            val movableStrategy = GoStrategy()

            // when
            car.move(movableStrategy)

            // then
            car.position shouldBe (carPositionBeforeMove + 1)
        }

        should("실패 - 이동할 수 없는 조건인 경우 이동하지 않는다") {
            // given
            val car = Car("자동차")
            val carPositionBeforeMove = car.position
            val unmovableStrategy = StopStrategy()

            // when
            car.move(unmovableStrategy)

            // then
            car.position shouldBe carPositionBeforeMove
        }
    }
})
