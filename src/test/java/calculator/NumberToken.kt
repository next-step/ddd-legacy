package calculator

data class NumberToken(
    val value: Int
) {
    init {
        require(value >= 0) {
            throw RuntimeException("negative number can't be calculated")
        }
    }

    operator fun plus(token: NumberToken): NumberToken =
        NumberToken(value + token.value)
}

data class NumberTokens(
    val tokens: List<NumberToken>
) {
    fun calculate(): Int = tokens.reduce { acc, numberToken -> acc + numberToken }.value
}

fun List<String>.getNumberTokens(): NumberTokens {
    val numberTokens = mapNotNull { it.toIntOrNull() }
        .map { NumberToken(it) }
        .ifEmpty {
            throw RuntimeException("only string text included")
        }

    return NumberTokens(numberTokens)
}
