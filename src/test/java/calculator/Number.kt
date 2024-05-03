package calculator

class Number(
    val number: Int,
) {
    companion object {
        fun create(stringNumber: String): Number {
            validate(stringNumber)
            return Number(stringNumber.toInt())
        }

        private fun validate(stringNumber: String) {
            val number = stringNumber.toIntOrNull()

            requireNotNull(number) { "숫자가 아닙니다." }
            require(number >= 0) { "숫자는 0보다 커야합니다." }
        }
    }
}
