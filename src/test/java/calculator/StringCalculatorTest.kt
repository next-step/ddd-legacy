package calculator

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class StringCalculatorTest : DescribeSpec({

    describe("StringCalculator 클래스의") {
        describe("add 메소드는") {
            context("빈 문자열이 주어지면") {
                it("0을 반환한다") {
                    val calculator = StringCalculator()

                    calculator.add("") shouldBe 0
                }
            }

            context("null이 주어지면") {
                it("0을 반환한다") {
                    val calculator = StringCalculator()

                    calculator.add(null) shouldBe 0
                }
            }

            context("숫자 하나의 문자열이 주어지면") {
                it("숫자를 반환한다") {
                    val calculator = StringCalculator()

                    calculator.add("10") shouldBe 10
                }
            }

            context("(,)를 구분자로 여러 숫자의 문자열이 주어지면") {
                it("합을 반환한다") {
                    val calculator = StringCalculator()

                    calculator.add("1,2") shouldBe 3
                }
            }

            context("(:)를 구분자로 여러 숫자의 문자열이 주어지면") {
                it("합을 반환한다") {
                    val calculator = StringCalculator()

                    calculator.add("1:3:5") shouldBe 9
                }
            }

            context("(,)와 (:) 구분자로 여러 숫자의 문자열이 주어지면") {
                it("합을 반환한다") {
                    val calculator = StringCalculator()

                    calculator.add("1:3:5,7") shouldBe 16
                }
            }

            context("커스텀 구분자가 주어지면") {
                it("커스텀 구분자로 문자열을 나눠 합을 반환한다") {
                    val calculator = StringCalculator()

                    calculator.add("//*\n1*2*3") shouldBe 6
                }
            }
        }
    }
})
