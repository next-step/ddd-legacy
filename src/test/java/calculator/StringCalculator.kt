package calculator

import java.util.regex.Pattern

class StringCalculator {
    fun add(
        text: String?,
    ): Int = if (text.isNullOrBlank()) {
        EMPTY_RESULT
    } else parseText(text = text)

    private fun parseText(text: String): Int {
        return pattern.matcher(text).let { matcher ->
            if (matcher.find()) {
                return sumByString(
                    delimiter = matcher.group(DELIMITER_GROUP_INDEX),
                    numbersText = matcher.group(DATA_GROUP_INDEX),
                )
            }

            sumByString(
                delimiter = DEFAULT_DELIMITER,
                numbersText = text,
            )
        }
    }

    private fun sumByString(
        delimiter: String,
        numbersText: String,
    ): Int = numbersText
        .split(regex = delimiter.toRegex())
        .sumOf { numberText -> numberText.toInt().requirePositive() }

    private fun Int.requirePositive(): Int =
        this.takeIf { it > NEGATIVE_NUMBER } ?: throw RuntimeException(ERROR_MESSAGE_NEGATIVE_NUMBER)

    companion object {
        private val pattern by lazy { Pattern.compile(EXTRACT_CUSTOM_DELIMITER_REGEX) }

        private const val DEFAULT_DELIMITER = "[,|:]"
        private const val EXTRACT_CUSTOM_DELIMITER_REGEX = "//(.)\\n(.*)"

        private const val ERROR_MESSAGE_NEGATIVE_NUMBER = "음수를 입력할 수 없습니다."

        private const val DELIMITER_GROUP_INDEX = 1
        private const val DATA_GROUP_INDEX = 2

        private const val EMPTY_RESULT = 0
        private const val NEGATIVE_NUMBER = -1
    }
}
