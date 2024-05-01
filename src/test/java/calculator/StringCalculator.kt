package calculator

class StringCalculator {
    fun add(
        text: String?,
    ): Int = if (text.isNullOrBlank()) {
        EMPTY_RESULT
    } else parseText(text = text)

    private fun parseText(text: String): Int {
        val parser = TextDelimiterParser(text)

        return sumByString(
            delimiter = parser.getDelimiter(),
            numbersText = parser.getNumbersText(),
        )
    }

    private fun sumByString(
        delimiter: Delimiter,
        numbersText: String,
    ): Int = numbersText
        .split(regex = delimiter.toRegex())
        .sumOf { numberText -> PositiveNumber(numberText.toInt()).number }

    companion object {
        private const val EMPTY_RESULT = 0
    }
}


