package calculator

class StringCalculator {
    fun add(text: String?): Int {
        val numberTokens = parseNumberToken(text)

        validateNumberTokens(numberTokens)

        return calculateNumberTokens(numberTokens)
    }

    private fun parseNumberToken(text: String?): List<Int>? =
        text?.split(",", ":")
            ?.mapNotNull { it.toIntOrNull() }
            ?.toList()

    private fun calculateNumberTokens(numberTokens: List<Int>?) =
        numberTokens?.sum() ?: 0

    private fun validateNumberTokens(numberTokens: List<Int>?) {
        if (numberTokens != null && numberTokens.any { it < 0 }) {
            throw RuntimeException("can't calculate negative number")
        }
    }
}