package racingcar

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows

class CarTest : DescribeSpec({
    describe("Car 클래스의") {
        describe("생성자는") {
            context("이름이 5글자를 초과할 시") {
                it("IllegalArgumentException 를 발생시킨다.") {
                    assertThrows<IllegalArgumentException> {
                        Car("경주하는자동차", 1)
                    }
                }
            }
        }

        describe("move 메소드는") {
            context("GoStrategy 가 주어지면") {
                it("자동차는 한칸 전진한다.") {
                    val car = Car.init("자동차1")
                    car.move(GoStrategy())
                    car.position shouldBe 1
                }
            }

            context("StopStrategy가 주어지면") {
                it("자동차는 정지한다") {
                    val car = Car.init("자동차2")
                    car.move(StopStrategy())
                    car.position shouldBe 0
                }
            }
        }
    }
})