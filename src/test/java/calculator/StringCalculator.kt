package calculator

class StringCalculator {

    private val calculators = listOf(
        SingleNumberCalculator(),
        FixedDelimiterCalculator(),
        CustomizedDelimiterCalculator(),
    )

    fun add(text: String?): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        val calculator = calculators.find { it.support(text) }

        return calculator?.calculate(text)
            ?: throw RuntimeException("invalid text")
    }
}
