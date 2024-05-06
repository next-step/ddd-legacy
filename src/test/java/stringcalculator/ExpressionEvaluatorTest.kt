package stringcalculator

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

class ExpressionEvaluatorTest : ShouldSpec({
    val evaluator = ExpressionEvaluator()

    should("성공 - 숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다") {
        // given
        val expression = Expression("1")

        // when
        val result = evaluator.evaluate(
            expression = expression,
            delimiters = emptySet()
        )

        // then
        result shouldBe 1
    }

    should("성공 - 숫자 두개를 구분자로 입력할 경우 두 숫자의 합을 반환한다") {
        // given
        val delimiter = Delimiter(",")
        val expression = Expression("1${delimiter.value}2")

        // when
        val result = evaluator.evaluate(
            expression = expression,
            delimiters = setOf(delimiter)
        )

        // then
        result shouldBe 3
    }

    should("실패 - 음수를 전달하는 경우 예외가 발생한다") {
        // given
        val delimiter = Delimiter(",")
        val expression = Expression("-1${delimiter.value}2")

        // when
        val exception = shouldThrowAny {
            evaluator.evaluate(
                expression = expression,
                delimiters = setOf(delimiter)
            )
        }

        // then
        exception.shouldBeTypeOf<RuntimeException>()
    }
})
