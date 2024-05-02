package calculator

@JvmInline
value class Formula private constructor(private val formula: String) {
    fun split(delimiter: Delimiter): List<Operand> = formula.split(delimiter.regex).map(Operand::from)

    companion object {
        private val customDelimiterPattern = Delimiter.customDelimiterPattern

        fun from(input: String): Formula {
            val formula = customDelimiterPattern.replace(input, "")
            return Formula(formula)
        }
    }
}
