package calculator

@JvmInline
value class Operand(val value: Int) {
    init {
        require(value >= 0) { "value must be positive" }
    }

    constructor(valueString: String) : this(
        valueString.toIntOrNull() ?: throw IllegalArgumentException("valueString must be a number")
    )

    operator fun plus(other: Operand): Operand {
        return Operand(value + other.value)
    }
}
