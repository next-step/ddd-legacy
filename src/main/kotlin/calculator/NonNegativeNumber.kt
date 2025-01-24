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

fun List<NonNegativeNumber>.sum(): Int = this.sumOf { it.value }