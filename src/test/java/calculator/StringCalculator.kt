package calculator

class StringCalculator {

    fun add(text: String?): Int {
        if (text.isNullOrBlank()) return 0
        val numbers = Numbers.create(TextParser.parse(text))
        return numbers.sum()
    }
}
