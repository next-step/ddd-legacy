package calculator

data class NonNegativeNumber(
    val value: Int
) {

    init {
        if (value < 0) {
            throw RuntimeException("숫자는 음이 아닌 정수이어야 합니다. ($value)")
        }
    }
}

fun Int.toNonNegativeNumber() = NonNegativeNumber(this)

fun String.toNonNegativeNumber() = this.toInt().toNonNegativeNumber()

fun List<Int>.toNonNegativeNumbers() = map { it.toNonNegativeNumber() }

fun List<NonNegativeNumber>.sum(): Int = this.sumOf { it.value }