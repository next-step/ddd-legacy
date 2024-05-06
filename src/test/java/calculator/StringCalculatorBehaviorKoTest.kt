package calculator

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.ints.shouldBeExactly

class StringCalculatorBehaviorKoTest : BehaviorSpec({
    lateinit var calculator: StringCalculator
    val textDelimiterParser = TextDelimiterParser()

    beforeTest {
        calculator = StringCalculator(textDelimiterParser)
    }

    Given("입력 값이 빈 문자열 또는 null") {
        When("문자열 계산기 실행 시") {
            Then("0을 반환해야 한다.") {
                forAll(
                    row(""),
                    row(null),
                ) { text ->
                    calculator.add(text) shouldBeExactly 0
                }
            }
        }
    }

    Given("입력 값이 숫자 하나") {
        When("문자열 계산기를 실행 시") {
            Then("해당 숫자를 반환.") {
                forAll(
                    row("1"),
                    row("11"),
                    row("0"),
                    row("99"),
                ) { text ->
                    calculator.add(text) shouldBeExactly text.toInt()
                }
            }
        }
    }

    Given("입력 값이 숫자 두개를 쉼표(,) 구분자") {
        When("문자열 계산기를 실행 시") {
            Then("두 숫자의 합을 반환") {
                calculator.add("1,2") shouldBeExactly 3
            }
        }
    }

    Given("구분자를 쉼표(,) 이외에 콜론(:)을 입력") {
        When("문자열 계산기를 실행 시") {
            Then("숫자의 합을 반환") {
                calculator.add("1,2:3") shouldBeExactly 6
            }
        }
    }

    Given("//와 \\n 문자 사이에 커스텀 구분자를 지정") {
        When("문자열 계산기를 실행 시") {
            forAll(
                row("//.\n1.2.3"),
                row("//;\n1;2;3"),
            ) { text ->
                Then("숫자의 합을 반환") {
                    calculator.add(text) shouldBeExactly 6
                }
            }
        }
    }

    Given("음수를 전달하는 경우") {
        When("문자열 계산기를 실행 시") {
            Then("RuntimeException 예외 처리를 한다.") {
                shouldThrow<RuntimeException> {
                    calculator.add("-1")
                }
            }
        }
    }

    Given("숫자가 아닌 수를 전달하는 경우") {
        When("문자열 계산기를 실행 시") {
            Then("IllegalArgument 예외 처리를 한다.") {
                forAll(
                    row("//.\naaa"),
                    row("//;\n;;"),
                ) { text ->
                    shouldThrowExactly<IllegalArgumentException> {
                        calculator.add(text)
                    }
                }
            }
        }
    }
})
