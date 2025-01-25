package calculator

@JvmInline
value class NonNegativeNumber(
    val value: Int
) {

    init {
        require(value >= LOWER_BOUND) {
            "숫자($value)는 $LOWER_BOUND 이상이어야 합니다."
        }
    }

    companion object {
        const val LOWER_BOUND = 0
    }
}

fun Int.toNonNegativeNumber() = NonNegativeNumber(this)

fun String.toNonNegativeNumber() = this.toInt().toNonNegativeNumber()

fun List<Int>.toNonNegativeNumbers() = map { it.toNonNegativeNumber() }

fun List<NonNegativeNumber>.sum(): Int = this.sumOf { it.value }
