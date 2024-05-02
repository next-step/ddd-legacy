package calculator

@JvmInline
value class Operand(val value: Int) {
    init {
        require(value >= 0) { "value must be positive" }
    }

    operator fun plus(other: Operand): Operand = Operand(value + other.value)

    companion object {
        fun from(valueString: String): Operand {
            require(valueString.toIntOrNull() != null) { "value must be a number" }
            return Operand(valueString.toInt())
        }
    }
}
