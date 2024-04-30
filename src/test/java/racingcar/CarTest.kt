package racingcar

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows

data class Car(
    val name: String,
    val position: Int
) {
    companion object {
        fun init(name: String): Car {
            return Car(name, 0)
        }
    }

    init {
        require(name.length <= 5)
        {
            throw IllegalArgumentException("자동차 이름은 5자 이하여야 합니다")
        }
    }

    fun move(condition: MovingStrategy): Car {
        return if (condition.isMovable()) this.copy(position = this.position + 1)
        else this
    }
}

class CarTest : DescribeSpec({
    describe("Car 클래스의") {
        describe("생성자는") {
            context("이름이 5글자가 넘는 값이 주어지면") {
                it("IllegalArgumentException이 발생한다") {
                    assertThrows<IllegalArgumentException> {
                        Car("도레미파솔라시도", 1)
                    }
                }
            }
        }

        describe("move 메소드는") {
            context("컨디션이 4 이상인 경우") {
                it("자동차는 전진한다") {
                    val movedCar = Car.init("이민준").move(GoStrategy())

                    movedCar.position shouldBe 1
                }
            }

            context("컨디션이 4 미만 경우") {
                it("자동차는 정지한다") {
                    val movedCar = Car.init("이민준").move(StopStrategy())

                    movedCar.position shouldBe 0
                }
            }
        }
    }
})
