package calculator

@JvmInline
value class Delimiter private constructor(
    val regex: Regex
) {

    constructor(value: String) : this(
        CUSTOM_DELIMITER
            .find(value)
            .extractDelimiterRegex()
    )

    companion object {

        private val DEFAULT_DELIMITER = Regex("""[,:]""")
        private val CUSTOM_DELIMITER = Regex("""//(?<delimiter>.)\\n.*""")

        private fun MatchResult?.extractDelimiterRegex(): Regex =
            this?.groups
                ?.get("delimiter")
                ?.value
                ?.let { Regex.escape(it) }
                ?.toRegex()
                ?: DEFAULT_DELIMITER
    }
}
