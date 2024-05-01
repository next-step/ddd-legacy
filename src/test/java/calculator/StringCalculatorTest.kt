package calculator

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

class StringCalculatorTest : ExpectSpec({

    val stringCalculator = StringCalculator()

    expect("빈 문자열을 입력할 경우 0을 반환해야 한다.") {
        stringCalculator.add("") shouldBe 0
    }

    expect("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.") {
        stringCalculator.add("1") shouldBe 1
    }

    expect("숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.") {
        stringCalculator.add("1,2,3") shouldBe 6
    }

    expect("구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.") {
        stringCalculator.add("1,2:3") shouldBe 6
    }

    expect("문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.") {
        shouldThrow<IllegalArgumentException> {
            stringCalculator.add("-1")
        }
    }

    expect("피연산자가 숫자가 아니면 RuntimeException 예외 처리를 한다.") {
        shouldThrow<IllegalArgumentException> {
            stringCalculator.add("test")
        }
    }

    expect("커스텀 구분자를 지정할 수 있다.") {
        stringCalculator.add("//;\n1;2;3") shouldBe 6
    }
})
