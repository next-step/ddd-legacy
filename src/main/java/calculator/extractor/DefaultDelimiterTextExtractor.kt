package calculator.extractor

import calculator.Number
import calculator.Numbers
import java.util.regex.Pattern

class DefaultDelimiterTextExtractor : TextDelimiterExtractor {

    override fun isSupport(text: String?): Boolean {
        return text?.let { COMMA_OR_COLON_AS_A_DELIMITER_PATTERN.matcher(it).find() } ?: false
    }

    override fun extract(text: String): Numbers {
        return Numbers(
            text
                .split(COMMA_OR_COLON_AS_A_DELIMITER_PATTERN_REGEX)
                .map { Number(it) }
        )
    }

    companion object {
        private val COMMA_OR_COLON_AS_A_DELIMITER_PATTERN = Pattern.compile(",|:")
        private val COMMA_OR_COLON_AS_A_DELIMITER_PATTERN_REGEX = COMMA_OR_COLON_AS_A_DELIMITER_PATTERN.toRegex()
    }
}
