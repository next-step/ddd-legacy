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
        if (!allowedFormat.matches(src)) {
            throw RuntimeException("잘못된 Input String 입니다. ($src)")
        }
    }

    companion object {
        private val allowedFormat = Regex("""(//.\n)?(?<target>[0-9]+(.[0-9]+)*)""")

        fun of(source: String?): InputString =
            source.takeUnless { it.isNullOrEmpty() }
                ?.let { InputString(it) }
                ?: InputString("0")
    }
}
