package calculator

data class NumberToken(
    val value: Int
) {
    init {
        if (value < 0) {
            throw RuntimeException("negative number can't be calculated")
        }
    }
}

data class NumberTokens(
    val tokens: List<NumberToken>
) {
    fun calculate() = tokens.sumOf { it.value }
}

fun List<String>.getNumberTokens(): NumberTokens {
    val numberTokens = mapNotNull { it.toIntOrNull() }
        .map { NumberToken(it) }
        .ifEmpty {
            throw RuntimeException("only string text included")
        }

    return NumberTokens(numberTokens)
}