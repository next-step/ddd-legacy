package stringcaculator

import java.lang.NumberFormatException

data class Number(val num: String) {

    init {
        try {
            checkNegative(num.toInt())
        } catch (ne: NumberFormatException) {
            throw RuntimeException("숫자타입만 입력가능합니다. $num")
        } catch (re: RuntimeException) {
            throw RuntimeException("음수는 포함될 수 없습니다. $num")
        } catch (e: Exception) {
            throw e
        }
    }

    private fun checkNegative(intNum: Int) {
        if (intNum < 0) {
            throw RuntimeException("음수는 포함될 수 없습니다")
        }
    }
}
