package stringcalculator

class StringCalculator() {

    fun add(input: String?): Int {
        if (input.isNullOrEmpty()) return 0

        val singleNumber = convertSingleNumber(input)
        singleNumber.map { return@add it }

        return 0
    }

    private fun convertSingleNumber(input: String): Result<Int> {
        return kotlin.runCatching { input.toInt() }
    }
}
