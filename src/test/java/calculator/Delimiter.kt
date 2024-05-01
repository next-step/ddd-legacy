package calculator

@JvmInline
value class Delimiter(private val delimiter: String) {
    fun toRegex(): Regex = delimiter.toRegex()
}
