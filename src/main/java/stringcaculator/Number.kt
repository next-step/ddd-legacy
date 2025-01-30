package stringcaculator

import java.lang.NumberFormatException

data class Number(val num: String) {

    init {
        val intNum = try {
            num.toInt()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("숫자타입만 입력가능합니다: $num")
        }

        require(intNum >= 0) { "음수는 포함될 수 없습니다: $num" }
    }
}
