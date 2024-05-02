package calculator

class StringCalculator {
    fun add(input: String): Int {
        if (input.isBlank()) return 0

        val delimiter = Delimiter.from(input)
        val formula = Formula.from(input)

        return formula.split(delimiter)
            .reduce { acc, operand -> acc + operand }
            .value
    }
}
