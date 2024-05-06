package calculator

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeExactly

class StringCalculatorKoTest : FunSpec({
    lateinit var calculator: StringCalculator
    val textDelimiterParser = TextDelimiterParser()

    beforeTest {
        calculator = StringCalculator(textDelimiterParser)
    }

    test("빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.") {
        listOf(null, "").forEach {
            calculator.add(it) shouldBeExactly 0
        }
    }

    test("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.") {
        listOf("1", "11", "0", "99").forEach {
            calculator.add(it) shouldBeExactly it.toInt()
        }
    }

    test("숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.") {
        calculator.add("1,2") shouldBeExactly 3
    }

    test("구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.") {
        calculator.add("1,2:3") shouldBeExactly 6
    }

    test("//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.") {
        listOf("//.\n1.2.3", "//;\n1;2;3").forEach { text ->
            calculator.add(text) shouldBeExactly 6
        }
    }

    test("문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.") {
        shouldThrow<RuntimeException> {
            calculator.add("-1")
        }
    }

    test("문자열 계산기에 숫자가 아닌 수를 전달하는 경우 IllegalArgument 예외 처리를 한다.") {
        listOf("//.\naaa", "//;\n;;").forEach { text ->
            shouldThrowExactly<IllegalArgumentException> {
                calculator.add(text)
            }
        }
    }
})
