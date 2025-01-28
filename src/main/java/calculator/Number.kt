package calculator

import calculator.exception.NumberFormatException

@JvmInline
value class Number(
    val value: Int,
) {
    constructor(value: String) : this(
        value.toIntOrNull() ?: throw NumberFormatException("Invalid number format: $value")
    )

    init {
        require(value >= ZERO.value) { "음수는 지원하지 않습니다" }
    }

    operator fun plus(number: Number) = Number(Math.addExact(value, number.value))

    companion object {
        val ZERO: Number = Number(0)
    }
}
