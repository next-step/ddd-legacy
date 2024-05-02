package calculator

class StringCalculator {

    private val calculatorNumberRangeValidator = CalculatorNumberRangeValidator()

    private val calculators = listOf(
        EmptyTextCalculator(),
        SingleNumberCalculator(calculatorNumberRangeValidator),
        FixedDelimiterCalculator(calculatorNumberRangeValidator),
        CustomizedDelimiterCalculator(calculatorNumberRangeValidator),
    )

    fun add(text: String?): Int {
        val calculator = calculators.find { it.support(text) }

        return calculator?.calculate(text)
            ?: throw RuntimeException("invalid text")
    }
}