package calculator

class StringCalculator {
    fun add(input: String): Int {
        if (input.isBlank()) return 0

        val (delimiter, formula) = Delimiter.from(input)
        val operands = delimiter.split(formula)

        return operands
            .reduce { acc, operand -> acc + operand }
            .value
    }
}
