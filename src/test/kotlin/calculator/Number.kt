package calculator

class Number(
    val number: Int,
) {
    companion object {
        fun create(number: Int): Number {
            require(number >= 0) { "숫자는 0보다 커야합니다." }
            return Number(number)
        }
    }
}
