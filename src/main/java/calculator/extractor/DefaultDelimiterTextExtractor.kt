package calculator.extractor

import calculator.Number
import calculator.Numbers
import java.util.regex.Pattern

class DefaultDelimiterTextExtractor : TextDelimiterExtractor {

    override fun isSupport(text: String?): Boolean {
        return text?.let { PATTERN.matcher(it).find() } ?: false
    }

    override fun extract(text: String): Numbers {
        return Numbers(
            text
                .split(REGEX)
                .map { Number(it) }
        )
    }

    companion object {
        private const val REGEX_PATTERN: String = ",|:"
        private val PATTERN = Pattern.compile(REGEX_PATTERN)
        private val REGEX = PATTERN.toRegex()
    }
}
