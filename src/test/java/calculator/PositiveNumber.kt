package calculator

@JvmInline
value class PositiveNumber(val number: Int) {
    init {
        require(number > NEGATIVE_NUMBER) {
            ERROR_MESSAGE_NEGATIVE_NUMBER
        }
    }

    companion object {
        private const val NEGATIVE_NUMBER = -1
        private const val ERROR_MESSAGE_NEGATIVE_NUMBER = "음수를 입력할 수 없습니다."
    }
}
