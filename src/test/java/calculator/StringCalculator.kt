package calculator

import java.util.regex.Pattern

class StringCalculator {
    fun add(text: String?): Int {
        if (text.isNullOrBlank()) {
            return EMPTY_RESULT
        }

        return Pattern.compile(EXTRACT_CUSTOM_DELIMITER_REGEX).matcher(text)
            .let { matcher ->
                when {
                    matcher.find() -> {
                        val customDelimiter = matcher.group(1)
                        matcher.group(2)
                            .split(regex = customDelimiter.toRegex())
                            .sumOf { it.toInt() }
                    }

                    else -> text.split(regex = DEFAULT_DELIMITER.toRegex())
                        .sumOf { split ->
                            split.toInt()
                                .takeIf { it > NEGATIVE_ONE }
                                ?: throw RuntimeException(ERROR_MESSAGE_NEGATIVE_NUMBER)
                        }
                }
            }
    }

    companion object Constant {
        private const val DEFAULT_DELIMITER = "[,|:]"
        private const val EXTRACT_CUSTOM_DELIMITER_REGEX = "//(.)\\n(.*)"

        private const val ERROR_MESSAGE_NEGATIVE_NUMBER = "음수를 입력할 수 없습니다."

        private const val EMPTY_RESULT = 0
        private const val NEGATIVE_ONE = -1
    }
}
