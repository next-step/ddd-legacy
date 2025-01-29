package stringcaculator


class DefaultParser : Parser {
    private val DEFAULT_DELIMITERS = arrayOf(",", ":")

    override fun parse(text: String): ParsedText =
        ParsedText(
            delimiters = DEFAULT_DELIMITERS,
            body = text
        )
}