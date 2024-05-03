package calculator

import java.util.regex.Pattern

class TextDelimiterParser(
    private val text: String,
) {
    private val matcher by lazy { pattern.matcher(text) }
    private val isFind by lazy { matcher.find() }

    fun getDelimiter(): Delimiter =
        if (isFind) Delimiter(matcher.group(DELIMITER_GROUP_INDEX))
        else DEFAULT_DELIMITER

    fun getNumbersText(): String =
        if (isFind) matcher.group(DATA_GROUP_INDEX)
        else text

    companion object {
        private val pattern by lazy { Pattern.compile(EXTRACT_CUSTOM_DELIMITER_REGEX) }
        private val DEFAULT_DELIMITER = Delimiter("[,|:]")

        private const val EXTRACT_CUSTOM_DELIMITER_REGEX = "//(.)\\n(.*)"
        private const val DELIMITER_GROUP_INDEX = 1
        private const val DATA_GROUP_INDEX = 2
    }
}
