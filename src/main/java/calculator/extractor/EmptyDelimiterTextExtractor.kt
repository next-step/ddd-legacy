package calculator.extractor

import calculator.Number
import calculator.Numbers

class EmptyDelimiterTextExtractor : TextDelimiterExtractor {

    override fun isSupport(text: String?): Boolean {
        return text.isNullOrBlank()
    }

    override fun extract(text: String): Numbers {
        return Numbers(listOf(Number(DEFAULT_EXTRACT_VALUE)))
    }

    companion object {
        private const val DEFAULT_EXTRACT_VALUE = 0
    }
}
