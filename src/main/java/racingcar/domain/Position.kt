package racingcar.domain

data class Position(
    val value: Int = DEFAULT_VALUE
) {
    fun forward() = copy(value + 1)

    companion object {
        private const val DEFAULT_VALUE = 0
    }
}
