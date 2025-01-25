package racingcar.domain

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import racingcar.strategy.move.RandomMoveStrategy
import java.lang.IllegalArgumentException

class CarTest : FreeSpec({

    "자동차의 이름의 길이는 0 이하일 수 없다" {
        shouldThrow<IllegalArgumentException> {
            Car(Name(""))
        }.run {
            message shouldBe "이름은 빈 값일 수 없습니다."
        }
    }

    "자동차의 이름의 길이는 6 이상일 수 없다" {
        shouldThrow<IllegalArgumentException> {
            Car(Name("a".repeat(6)))
        }.run {
            message shouldBe "이름의 길이는 1 ~ 5 사이여야 합니다."
        }
    }

    "자동차의 이름의 길이는 1 ~ 5 글자여야 한다" - {
        for (i in 1..5) {
            "글자 길이가 $i 인 경우" - {
                shouldNotThrow<IllegalArgumentException> {
                    Car(Name("a".repeat(i)))
                }
            }
        }
    }

    "자동차의 위치는 0 부터 시작된다" {
        Car(name = Name("현대")).run {
            position shouldBe Position(0)
        }
    }

    "자동차는 숫자 값 4 이상이 주어진 경우 한 칸 움직일 수 있다" {
        Car(Name("현대")).moveForward(RandomMoveStrategy({ 4 })).run {
            position shouldBe Position(1)
        }
    }

    "자동차는 숫자 값 4 미만이 주어진 경우 움직일 수 없다" {
        Car(Name("현대")).moveForward(RandomMoveStrategy({ 3 })).run {
            position shouldBe Position(0)
        }
    }
})
