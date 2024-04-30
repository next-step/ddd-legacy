package calculator

class StringCalculator {
    fun add(text: String?): Int {
        if (text.isNullOrBlank()) {
            return 0
        }
        return 1;
    }
}
