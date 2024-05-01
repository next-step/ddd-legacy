package calculator

private const val FORMULA_SPLITTER = "[,:]"

class StringCalculator {
    fun add(formula: String): Int {
        if (formula.isBlank()) {
            return 0
        }

        return formula.split(FORMULA_SPLITTER.toRegex())
            .map { Operand(it) }
            .reduce { acc, operand -> acc + operand }
            .value
    }
}
