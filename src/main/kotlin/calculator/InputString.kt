package calculator

data class InputString private constructor(
    val src: String
) {

    init {
        require(ALLOWED_FORMAT.matches(src)) {
            "잘못된 Input String 입니다. ($src)"
        }
    }

    private val target: String =
        ALLOWED_FORMAT
            .find(src)
            .extractTarget()

    private fun MatchResult?.extractTarget(): String =
        this?.groups
            ?.get("target")
            ?.value
            ?: throw IllegalArgumentException("잘못된 Input String 입니다. ($src)")

    fun toNonNegativeInts(): List<NonNegativeInt> {
        val delimiter = Delimiter(src)
        return target.split(delimiter.regex)
            .map { NonNegativeInt(it.toInt()) }
    }

    companion object {

        private val ALLOWED_FORMAT = Regex("""(//.\\n)?(?<target>[0-9]+(.[0-9]+)*)""")

        fun of(source: String?): InputString =
            source.takeUnless { it.isNullOrEmpty() }
                ?.let { InputString(it) }
                ?: InputString("0")
    }
}
