package calculator

import calculator.extractor.TextDelimiterExtractorFactory

data class StringCalculator(
    private val factory: TextDelimiterExtractorFactory,
) {

    fun add(text: String?): Int {
        return factory.get(text)
            .extract(text ?: "")
            .sum().value
    }
}
