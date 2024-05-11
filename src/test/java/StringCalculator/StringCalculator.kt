package StringCalculator

class StringCalculator {
    fun add(inputText: String?): Int {
        val numbers = InputParser.parse(inputText).map { Number.create(it) }
        return numbers.sum()
    }
}