package stringcalculator

import stringcalculator.ExpressionMatcher.Companion.Expression

class NumberTokenizer {

    fun tokenize(expression: Expression): List<Int> {
        val numberTokens = splitNumberTokens(expression.input, expression.delimiters)
        val numbers = numberTokens.map { it.toInt() }

        return numbers
    }

    private fun splitNumberTokens(input: String, delimiters: List<String>): List<String> {
        return if (delimiters.isEmpty()) listOf(input)
        else input.split(delimiters.joinToString("|").toRegex())
    }
}