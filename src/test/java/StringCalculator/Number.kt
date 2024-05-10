package StringCalculator

object Number {
    fun create(text: String): Int {

        val number = text.toIntOrNull() ?: 0

        require(number >= 0) {
            throw RuntimeException("음수 값이 포함되어 있습니다.")
        }

        return number
    }
}