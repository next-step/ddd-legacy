package calculator

class StringCalculator {
    fun add(input: String): Int {
        if (input.isBlank()) return 0

        val (delimiter, formula) = parseInput(input)
        return formula.split(delimiter)
            .map { Operand(it) }
            .reduce { acc, operand -> acc + operand }
            .value
    }

    private fun parseInput(input: String): Pair<Regex, String> {
        CUSTOM_DELIMITER_REGEX.find(input)?.let {
            val (delimiter, value) = it.destructured
            return delimiter.toRegex() to value
        }

        return DEFAULT_DELIMITER_REGEX to input
    }

    companion object {
        private val DEFAULT_DELIMITER_REGEX = "[,:]".toRegex()
        private val CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)".toRegex()
    }
}
