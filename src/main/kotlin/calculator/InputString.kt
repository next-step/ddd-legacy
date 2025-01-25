package calculator

data class InputString private constructor(
    val src: String
) {

    val target = allowedFormat.find(src)
        ?.groups
        ?.get("target")
        ?.value
        ?: throw RuntimeException("잘못된 Input String 입니다. ($src)")

    init {
        require(allowedFormat.matches(src)) {
            "잘못된 Input String 입니다. ($src)"
        }
    }

    fun toNonNegativeInts(): List<NonNegativeInt> {
        val delimiter = Delimiter(src)
        return target.split(delimiter.regex)
            .map { NonNegativeInt(it.toInt()) }
    }

    companion object {

        private val allowedFormat = Regex("""(//.\\n)?(?<target>[0-9]+(.[0-9]+)*)""")

        fun of(source: String?): InputString =
            source.takeUnless { it.isNullOrEmpty() }
                ?.let { InputString(it) }
                ?: InputString("0")
    }
}
