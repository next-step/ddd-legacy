package calculator

class SingleNumberCalculator : StringCalculateStrategy {
    override fun support(text: String): Boolean =
        text.length == 1 && text.toIntOrNull() != null

    override fun calculate(text: String): NumberToken =
        listOf(text).getNumberTokens()
            .let { it.calculate() }
}
