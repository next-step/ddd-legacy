package calculator.extractor

import calculator.Number
import calculator.Numbers
import java.util.regex.Pattern

class NonDelimitedTextExtractor : TextDelimiterExtractor {

    override fun isSupport(text: String?): Boolean {
        return text?.let { PATTERN.matcher(it).find() } ?: false
    }

    override fun extract(text: String): Numbers {
        return Numbers(listOf(Number(text)))
    }

    companion object {
        private const val REGEX_PATTERN: String = "^[0-9]{1,9}$"
        private val PATTERN = Pattern.compile(REGEX_PATTERN)
    }
}
