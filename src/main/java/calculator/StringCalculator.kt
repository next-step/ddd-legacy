package calculator

class StringCalculator(
    private val textDelimiterParser: TextDelimiterParser,
) {
    fun add(
        text: String?,
    ): Int = if (text.isNullOrBlank()) {
        EMPTY_RESULT
    } else {
        getSumByNumbersText(text)
    }

    private fun getSumByNumbersText(text: String): Int {
        val separateResult = separateText(text)

        return sumByString(
            delimiter = separateResult.delimiter,
            numbersText = separateResult.numbersText,
        )
    }

    private fun separateText(text: String): SeparateResult = textDelimiterParser.separateText(text)

    private fun sumByString(
        delimiter: Delimiter,
        numbersText: String,
    ): Int = numbersText
        .split(delimiter.toRegex())
        .map { it.toInt() }
        .sumOf { PositiveNumber(it).number }

    companion object {
        private const val EMPTY_RESULT = 0
    }
}
