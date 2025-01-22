package stringcalculator

class StringCalculator(
    private val text: String?
) {

    fun sum(): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        return text.toInt()
    }

}
