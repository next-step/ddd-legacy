package calculator.extractor

import calculator.exception.TextDelimiterExtractorException

class TextDelimiterExtractorFactory(
    private val textDelimiterExtractors: List<TextDelimiterExtractor>,
) {

    fun get(text: String?): TextDelimiterExtractor {
        return textDelimiterExtractors.firstOrNull {
            it.isSupport(text)
        } ?: throw TextDelimiterExtractorException("Unsupported text format: $text")
    }
}
