package calculator

import java.lang.IllegalArgumentException

data class NumberToken(
    val value: Int
) {
    init {
        require(value >= 0) {
            throw IllegalArgumentException("negative number can't be calculated")
        }
    }

    operator fun plus(token: NumberToken): NumberToken =
        NumberToken(value + token.value)
}

data class NumberTokens(
    val tokens: List<NumberToken>
) {
    fun calculate(): NumberToken = tokens.reduce { acc, numberToken -> acc + numberToken }
}

fun List<String>.getNumberTokens(): NumberTokens {
    val numberTokens = mapNotNull { it.toIntOrNull() }
        .map { NumberToken(it) }
        .ifEmpty {
            throw IllegalArgumentException("only string text included")
        }

    return NumberTokens(numberTokens)
}
