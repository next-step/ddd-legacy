package calculator

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertDoesNotThrow

class NumbersTest : DescribeSpec(
    {
        describe("Numbres 클래스의") {
            describe("create 메소드는") {
                context("Int 타입의 숫자가 주어지면") {
                    it("Number 인스턴스로 변환해서 상태를 가진다") {
                        assertDoesNotThrow {
                            Numbers.create(listOf(1, 2, 3))
                        }
                    }
                }
            }

            describe("sum 메소드는") {
                it("모든 숫자의 합을 반환한다") {
                    val numbers = Numbers.create(listOf(1, 2, 3))

                    numbers.sum() shouldBe 6
                }
            }
        }
    },
)
