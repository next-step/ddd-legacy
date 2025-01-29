package stringcaculator

import java.util.regex.Pattern

class Calculator() {
    private val ZERO = 0
    private val parsers = listOf(CustomParser(), DefaultParser())


    fun add(text: String?): Int {
        if (text.isNullOrEmpty()) {
            return ZERO
        }

        val parsedText = parsers.firstNotNullOf { parser ->
            parser.parse(text)
        }
        return Numbers.generateNumbers(parsedText.body, parsedText.delimiters).sum()
    }

}
