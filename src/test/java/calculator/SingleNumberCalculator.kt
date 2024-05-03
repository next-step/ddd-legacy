package calculator

class SingleNumberCalculator(
    private val calculatorNumberRangeValidator: CalculatorNumberRangeValidator
) : StringCalculateStrategy {
    override fun support(text: String?): Boolean =
        text != null && text.length == 1 && text.toIntOrNull() != null


    override fun calculate(text: String): Int {
        calculatorNumberRangeValidator.validateTokens(listOf(text))

        return text.toInt()
    }
}
