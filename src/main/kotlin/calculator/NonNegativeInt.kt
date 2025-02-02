package calculator

@JvmInline
value class NonNegativeInt(
    val value: Int
) {

    init {
        require(value >= LOWER_BOUND) {
            "숫자($value)는 $LOWER_BOUND 이상이어야 합니다."
        }
    }

    companion object {
        private const val LOWER_BOUND = 0
    }
}

fun List<NonNegativeInt>.sum(): Int = this.sumOf { it.value }
