package calculator.extractor

import calculator.Number
import calculator.Numbers
import calculator.exception.TextDelimiterExtractorException
import java.util.regex.Pattern

class CustomDelimiterTextExtractor : TextDelimiterExtractor {

    override fun isSupport(text: String?): Boolean {
        return text?.let { PATTERN.matcher(it).find() } ?: false
    }

    override fun extract(text: String): Numbers {
        val matcher = PATTERN.matcher(text)

        return if (matcher.find()) {
            val customDelimiter = matcher.group(1)

            Numbers(
                matcher.group(2)
                    .split(customDelimiter)
                    .map { Number(it) }
            )
        } else {
            throw TextDelimiterExtractorException("Unsupported text format: $text")
        }
    }

    companion object {
        private const val REGEX_PATTERN: String = "//(.)\n(.*)"
        private val PATTERN = Pattern.compile(REGEX_PATTERN)
    }
}
