package calculator

class StringCalculator(
    private val textDelimiterParser: TextDelimiterParser
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
        .sumOf { numberText ->
            numberText.toIntOrThrowIllegalArgumentException()
        }

    private fun String.toIntOrThrowIllegalArgumentException(): Int =
        this.toIntOrNull()?.let { PositiveNumber(it).number }
            ?: throw IllegalArgumentException(ERROR_MESSAGE_NOT_NUMBERS_TEXT)

    companion object {
        private const val EMPTY_RESULT = 0
        private const val ERROR_MESSAGE_NOT_NUMBERS_TEXT = "숫자로 구성된 문자열만 있어야 합니다."
    }
}
