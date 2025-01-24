package calculator

data class Delimiter private constructor(
    val value: Regex
) {

    constructor(value: String) : this(
        CUSTOM_DELIMITER.find(value)
            ?.groups
            ?.get("delimiter")
            ?.value
            ?.toRegex()
            ?: DEFAULT_DELIMITER
    )

    companion object {

        private val DEFAULT_DELIMITER = Regex("""[,:]""")
        private val CUSTOM_DELIMITER = Regex("""//(?<delimiter>.)\n.*""")
    }
}
