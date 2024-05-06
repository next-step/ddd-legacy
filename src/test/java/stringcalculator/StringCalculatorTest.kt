package stringcalculator

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class StringCalculatorTest : ShouldSpec({
    val calculator = StringCalculator(
        parser = Parser(),
        expressionEvaluator = ExpressionEvaluator()
    )

    should("성공 - 구분자를 쉼표(,) 콜론(:)을 사용할 수 있다.") {
        // given
        val input = "1,2:3"

        // when
        val result = calculator.calculate(input)

        // then
        result shouldBe 6
    }

    should("성공 - 빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다") {
        // given
        val input = null

        // when
        val result = calculator.calculate(input)

        // then
        result shouldBe 0
    }
})
