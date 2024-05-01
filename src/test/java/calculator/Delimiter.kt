package calculator

@JvmInline
value class Delimiter private constructor(private val delimiter: Regex) {
    fun split(input: String): List<Operand> = input.split(delimiter).map(::Operand)

    companion object {
        private val defaultDelimiter = "[,:]".toRegex()
        private val customDelimiterPattern = "//(.)\n".toRegex()

        fun from(input: String): Pair<Delimiter, String> {
            customDelimiterPattern.find(input)?.let {
                val customDelimiter = it.groupValues[1].toRegex()
                return Delimiter(customDelimiter) to input
            }
            return Delimiter(defaultDelimiter) to input
        }
    }
}
