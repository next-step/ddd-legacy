package calculator.extractor

import calculator.Numbers

interface TextDelimiterExtractor {

    fun isSupport(text: String?): Boolean

    fun extract(text: String): Numbers
}
