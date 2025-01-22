package stringcalculator

import java.util.regex.Pattern

class StringCalculator(
    private val text: String?
) {

    fun sum(): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        return when {
            hasNegativeNumber() -> throw IllegalArgumentException()
            hasCustomDelimiter() -> sumWithCustomDelimiter()
            else -> sumWithDefaultDelimiters()
        }
    }

    private fun hasNegativeNumber() :Boolean{
        return Pattern.compile("-\\d+").matcher(text).find()
    }

    private fun hasCustomDelimiter(): Boolean {
        return Pattern.compile("//(.)\n(.*)").matcher(text).find()
    }

    private fun sumWithCustomDelimiter(): Int {
        val matcher = Pattern.compile("//(.)\n(.*)").matcher(text)
        matcher.find()

        val customDelimiter: String = matcher.group(1)
        return matcher.group(2).split(customDelimiter, ",", ":").sumOf { it.toInt() }
    }

    private fun sumWithDefaultDelimiters(): Int {
        val splits = text!!.split(",", ":")
        if (splits.size <= 1) {
            return text.toInt()
        }

        return splits.sumOf { it.toInt() }
    }
}
