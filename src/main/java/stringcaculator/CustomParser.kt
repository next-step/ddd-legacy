package stringcaculator

import java.util.regex.Pattern

class CustomParser : Parser {
    private val CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)")
    private val DELIMITER_GROUP = 1
    private val BODY_GROUP = 2

    override fun parse(text: String): ParsedText? =
        CUSTOM_DELIMITER_PATTERN.matcher(text)
            .takeIf { it.find() }
            ?.let { matcher ->
                ParsedText(
                    delimiters = arrayOf(matcher.group(DELIMITER_GROUP)),
                    body = matcher.group(BODY_GROUP)
                )
            }
}