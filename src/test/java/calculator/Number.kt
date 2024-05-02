package calculator

class Number(
    val number: Int
) {
    init {
        require(number >= 0) { "숫자는 0보다 커야합니다." }
    }
}
