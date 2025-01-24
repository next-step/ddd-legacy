package calculator

fun InputString.toNonNegativeNumbers(): List<NonNegativeNumber> {
    val delimiter = Delimiter(src)
    return target.split(delimiter.regex)
        .map { it.toNonNegativeNumber() }
}
