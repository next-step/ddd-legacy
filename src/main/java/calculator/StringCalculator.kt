package calculator

import calculator.extractor.TextDelimiterExtractorFactory

class StringCalculator(
    private val factory: TextDelimiterExtractorFactory,
) {

    fun add(text: String?): Int {
        return factory.get(text)
            .extract(text ?: "")
            .sum().value
    }
}
