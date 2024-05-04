package calculator

class StringCalculator {
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

    private fun separateText(text: String): SeparateResult = TextParserUtils.separateText(text)

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
