package racingcar

data class Car(val name: String) {
    var currentPosition = 0
        private set

    init {
        if (name.length > 5) {
            throw IllegalArgumentException()
        }
    }

    fun move(position: Int) {
        if (position < 0 || position > 9) {
            throw IllegalArgumentException("position 은 0 ~ 9 사이의 값만 가능합니다.")
        }

        if (position < 4) {
            return
        }

        currentPosition += position
    }
}
