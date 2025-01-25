package racingcar.domain

data class Name(
    val value: String
) {
    init {
        require(value.isNotBlank()) { "이름은 빈 값일 수 없습니다." }
        require(value.length in MINIMUM_LENGTH..MAXIMIN_LENGTH) { "이름의 길이는 1 ~ 5 사이여야 합니다." }
    }

    companion object {
        private const val MINIMUM_LENGTH = 1
        private const val MAXIMIN_LENGTH = 5
    }
}
