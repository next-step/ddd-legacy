package calculator

@JvmInline
value class Delimiter(private val delimiter: String) {
    fun toRegex(): Regex = if (delimiter.isEscape()) {
        delimiter.toRegex(RegexOption.LITERAL)
    } else {
        delimiter.toRegex()
    }

    private fun String.isEscape(): Boolean = this in escapeCharacter

    companion object {
        private val escapeCharacter = listOf(".", "^", "$", "*", "+", "?", "\\", "|")
    }
}
