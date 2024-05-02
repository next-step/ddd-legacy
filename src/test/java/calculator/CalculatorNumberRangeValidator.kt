package calculator

class CalculatorNumberRangeValidator {

    fun validateTokens(tokens: List<String>) {
        val numberTokens = tokens.mapNotNull { it.toIntOrNull() }

        numberTokens.forEach {
            if (it < 0) {
                throw RuntimeException("invalid range number")
            }
        }

        numberTokens.ifEmpty {
            throw RuntimeException("only string text included")
        }
    }

}