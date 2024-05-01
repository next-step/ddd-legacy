package calculator

class StringCalculator {
    fun add(text: String?): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        if (text.length == 1) {
            return text.toInt()
        }

        return -1
    }
}
