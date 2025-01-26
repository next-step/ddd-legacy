package stringcalculator

class StringCalculator() {

    fun add(input: String?): Int {
        if (input.isNullOrEmpty()) return 0

        val singleNumber = convertSingleNumber(input)
        singleNumber.map { return@add it }

        val tokens = splitInput(input, listOf(","))
        val numberTokens = tokens.map { it.toInt() }

        return numberTokens.sum()
    }

    private fun convertSingleNumber(input: String): Result<Int> {
        return kotlin.runCatching { input.toInt() }
    }

    private fun splitInput(input: String, delimiters: List<String>): List<String> {
        return input.split(delimiters.joinToString("|").toRegex())
    }
}
