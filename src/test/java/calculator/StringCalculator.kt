package calculator

class StringCalculator {
    fun add(text: String?): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        return text.split(regex = "[,|:]".toRegex())
            .sumOf { it.toInt() }
    }
}
