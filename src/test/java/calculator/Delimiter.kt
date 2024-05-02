package calculator

@JvmInline
value class Delimiter(val regex: Regex) {
    companion object {
        private val defaultDelimiter = "[,:]".toRegex()
        val customDelimiterPattern = "//(.)\n".toRegex()

        fun from(input: String): Delimiter {
            customDelimiterPattern.find(input)?.let {
                val customDelimiter = it.groupValues[1].toRegex()
                return Delimiter(customDelimiter)
            }
            return Delimiter(defaultDelimiter)
        }
    }
}
