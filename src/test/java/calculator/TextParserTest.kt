package calculator

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class TextParserTest : DescribeSpec({
    describe("TextParser 클래스의") {
        describe("parse 메소드는") {
            context("(,)를 구분자로 여러 숫자의 문자열이 주어지면") {
                it("문자열을 리스트로 변환한다") {
                    TextParser.parse("1,2,3") shouldBe listOf("1", "2", "3")
                }
            }

            context("(:)를 구분자로 여러 숫자의 문자열이 주어지면") {
                it("문자열을 리스트로 변환한다") {
                    TextParser.parse("1:2:3") shouldBe listOf("1", "2", "3")
                }
            }

            context("(,)와 (:) 구분자로 여러 숫자의 문자열이 주어지면") {
                it("문자열을 리스트로 변환한다") {
                    TextParser.parse("1:2,3") shouldBe listOf("1", "2", "3")
                }
            }

            context("커스텀 구분자가 주어지면") {
                it("커스텀 구분자로 문자열을 나눠 리스트로 변환한다") {
                    TextParser.parse("//;\n1;2;3") shouldBe listOf("1", "2", "3")
                }
            }
        }
    }
})
