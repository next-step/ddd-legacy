package stringcaculator

interface Parser {
    fun parse(text: String): ParsedText?
}