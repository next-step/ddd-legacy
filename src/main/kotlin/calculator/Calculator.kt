package calculator

class Calculator {

    fun calculate(target: String?): Int =
        InputString.of(target)
            .toNonNegativeInts()
            .sum()
}
