package stringcalculator

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class ParserTest : ShouldSpec({
    val parser = Parser()

    should("성공 - // 와 \\n 사이에 커스텀 구분자를 지정할 수 있다") {
        // given
        val input = "//;\n"

        // when
        val result = parser.parse(input)

        // then
        result.customDelimiters.first() shouldBe Delimiter(";")
    }

    should("성공 - 파싱된 결과에서는 커스텀 구분자 지정 구문이 제거되어야 한다") {
        // given
        val input = "//;\n1,2"

        // when
        val result = parser.parse(input)

        // then
        result.customDelimiters.first() shouldBe Delimiter(";")
        result.expression.value shouldBe "1,2"
    }

    should("실패 - // 와 \\n 사이에 커스텀 구분자가 없는 경우 예외가 발생한다") {
        // given
        val input = "//\n"

        // when
        val exception = shouldThrowAny {
            parser.parse(input)
        }

        // then
        exception.shouldBeTypeOf<RuntimeException>()
    }
})
