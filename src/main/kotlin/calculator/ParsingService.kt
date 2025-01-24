package calculator

fun InputString.toNonNegativeNumber(): List<NonNegativeNumber> {
    val delimiter = Delimiter(src)
    return target
        .split(delimiter.regex)
        .map {
            runCatching { it.toInt() }
                .onFailure { throw RuntimeException("") }
                .getOrNull()!!
                .let { NonNegativeNumber(it) }
        }
}
