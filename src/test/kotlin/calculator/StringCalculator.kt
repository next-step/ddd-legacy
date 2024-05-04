package calculator

class StringCalculator {
    fun add(text: String?): Int {
        if (text.isNullOrBlank()) return 0

        val numberSources =
            TextParser.parse(text).map {
                it.toIntOrNull() ?: throw IllegalArgumentException("숫자가 아닌 값이 포함되어 있습니다.")
            }

        val numbers = Numbers.create(numberSources)

        return numbers.sum()
    }
}
