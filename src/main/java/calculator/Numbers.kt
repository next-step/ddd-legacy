package calculator

@JvmInline
value class Numbers(
    private val values: List<Number>,
) {

    fun sum() = values.fold(Number.ZERO, Number::plus)
}
