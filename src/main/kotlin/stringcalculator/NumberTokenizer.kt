package stringcalculator

import stringcalculator.ExpressionAnalyzer.Companion.Expression

class NumberTokenizer {

    fun tokenize(expression: Expression): List<Int> {
        val numberTokens = splitNumberTokens(expression.input, expression.delimiters)
        val numbers = numberTokens.map { it.toInt() }

        return numbers
    }

    private fun splitNumberTokens(input: String, delimiters: List<String>): List<String> {
        return input.split(delimiters.joinToString("|").toRegex())
    }
}