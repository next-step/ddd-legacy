package calculator

class StringCalculator {

    fun add(text: String?): Int {
        if (text.isNullOrBlank()) return 0

        val parser = TextParser()

        return parser.parse(text).sum()
    }
}
