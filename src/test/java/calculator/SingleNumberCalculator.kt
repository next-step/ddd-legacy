package calculator

class SingleNumberCalculator : StringCalculateStrategy {
    override fun support(text: String?): Boolean =
        text != null && text.length == 1 && text.toIntOrNull() != null

    override fun calculate(text: String): Int =
        listOf(text).getNumberTokens()
            .let { it.calculate() }
}
