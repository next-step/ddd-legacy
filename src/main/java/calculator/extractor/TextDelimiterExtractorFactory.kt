package calculator.extractor

import calculator.exception.TextDelimiterExtractorException

class TextDelimiterExtractorFactory(
    private val factories: List<TextDelimiterExtractor>,
) {

    fun get(text: String?): TextDelimiterExtractor {
        return factories.firstOrNull {
            it.isSupport(text)
        } ?: throw TextDelimiterExtractorException("Unsupported text format: $text")
    }
}
