package calculator

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class NumberTest : DescribeSpec({
    describe("Number 클래스의") {
        describe("create 메소드는") {
            context("음수가 주어지면") {
                it("IllegalArgumentException을 던진다") {
                    val exception =
                        shouldThrow<IllegalArgumentException> {
                            Number.create(-1)
                        }

                    exception.message shouldBe "숫자는 0보다 커야합니다."
                }
            }
        }
    }
})
