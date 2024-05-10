package StringCalculator

object InputParser {

    private const val DEFAULT_DELIMITERS = "[,:]"
    private val CUSTOM_DELIMITER_PATTERN = Regex("//(.)\n(.*)")

    fun parse(inputText: String?): List<String> {
        if (inputText.isNullOrEmpty()) {
            return emptyList()
        }

        val (delimiter, numbersString) = extractDelimiterAndNumbersString(inputText)
        return numbersString.split(delimiter)
    }

    private fun extractDelimiterAndNumbersString(text: String): Pair<Regex, String> {
        val matchResult = CUSTOM_DELIMITER_PATTERN.find(text)

        return if (matchResult != null) {
            val customDelimiter = matchResult.groupValues[1]
            val numbersString = matchResult.groupValues[2]
            val escapedDelimiter = Regex.escape(customDelimiter)
            Regex(escapedDelimiter) to numbersString
        } else {
            Regex(DEFAULT_DELIMITERS) to text
        }
    }
}