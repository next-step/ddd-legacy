package calculator

data class InputString private constructor(
    val value: String
) {

    val target = allowedFormat.find(value)
        ?.groups
        ?.get("target")
        ?.value
        ?: throw RuntimeException("잘못된 Input String 입니다. ($value)")

    init {
        if (!allowedFormat.matches(value)) {
            throw RuntimeException("잘못된 Input String 입니다. ($value)")
        }
    }

    companion object {
        private val allowedFormat = Regex("""(//.\n)?(?<target>[0-9]+(.[0-9]+)*)""")

        fun of(input: String?): InputString =
            input.takeUnless { it.isNullOrEmpty() }
                ?.let { InputString(it) }
                ?: InputString("0")
    }
}