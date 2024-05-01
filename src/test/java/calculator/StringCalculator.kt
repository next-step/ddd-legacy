package calculator

import java.util.regex.Pattern

class StringCalculator {
    fun add(text: String?): Int {
        if (text.isNullOrBlank()) {
            return 0
        }

        return Pattern.compile("//(.)\n(.*)").matcher(text)
            .let { matcher ->
                when {
                    matcher.find() -> {
                        val customDelimiter = matcher.group(1)
                        matcher.group(2)
                            .split(regex = customDelimiter.toRegex())
                            .sumOf { it.toInt() }
                    }

                    else -> text.split(regex = "[,|:]".toRegex())
                        .sumOf { s ->
                            s.toInt().takeIf { it > -1 } ?: throw RuntimeException("음수를 입력할 수 없습니다.")
                        }
                }
            }
    }
}
