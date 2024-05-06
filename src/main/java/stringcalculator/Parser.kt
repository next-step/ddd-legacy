package stringcalculator

class Parser {
    fun parse(input: String): ParseResult {
        val customDelimiters = customDelimiterRegex.findAll(input)
            .map(::parseCustomDelimiter)
            .toSet()

        val expression = input.replace(customDelimiterRegex, "")

        return ParseResult(
            customDelimiters = customDelimiters,
            expression = Expression(expression)
        )
    }

    private fun parseCustomDelimiter(matchResult: MatchResult): Delimiter {
        val result = matchResult.groups[1]

        if (result == null || result.value.isEmpty()) {
            throw RuntimeException("커스텀 구분자를 추출할 수 없습니다.")
        }

        return Delimiter(result.value)
    }

    data class ParseResult(
        val customDelimiters: Set<Delimiter>,
        val expression: Expression
    )

    companion object {
        private val customDelimiterRegex = "//(.*?)\\n".toRegex()
    }
}
