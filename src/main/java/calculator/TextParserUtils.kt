package calculator

import java.util.regex.Matcher
import java.util.regex.Pattern

object TextParserUtils {
    private const val EXTRACT_CUSTOM_DELIMITER_REGEX = "//(.)\\n(.*)"
    private const val DELIMITER_GROUP_INDEX = 1
    private const val DATA_GROUP_INDEX = 2

    private val pattern = Pattern.compile(EXTRACT_CUSTOM_DELIMITER_REGEX)
    private val DEFAULT_DELIMITER = Delimiter("[,|:]")

    fun separateText(text: String): SeparateResult {
        val matcher = getMatcher(text)
        val isFind = matcher.find()

        val delimiter = getDelimiter(
            isFind = isFind,
            matcher = matcher,
        )

        val numbersText = getNumbersText(
            isFind = isFind,
            matcher = matcher,
            text = text,
        )

        return SeparateResult(
            delimiter = delimiter,
            numbersText = numbersText,
        )
    }

    private fun getMatcher(text: String): Matcher {
        return pattern.matcher(text)
    }

    private fun getDelimiter(
        isFind: Boolean,
        matcher: Matcher,
    ) = if (isFind) {
        Delimiter(matcher.group(DELIMITER_GROUP_INDEX))
    } else {
        DEFAULT_DELIMITER
    }

    private fun getNumbersText(
        isFind: Boolean,
        matcher: Matcher,
        text: String,
    ) = if (isFind) {
        matcher.group(DATA_GROUP_INDEX)
    } else {
        text
    }
}

data class SeparateResult(
    val delimiter: Delimiter,
    val numbersText: String,
)
