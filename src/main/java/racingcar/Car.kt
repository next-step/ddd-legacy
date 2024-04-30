package racingcar

data class Car(val name: String) {
    var currentPosition = 0
        private set

    init {
        if (name.length > 5) {
            throw IllegalArgumentException()
        }
    }

    fun move(condition: Int) {
        if (condition < 0 || condition > 9) {
            throw IllegalArgumentException("position 은 0 ~ 9 사이의 값만 가능합니다.")
        }

        move(
            object : MovingStrategy {
                override fun movable(): Boolean {
                    return condition >= 4
                }
            },
        )
    }

    private fun move(strategy: MovingStrategy) {
        if (strategy.movable()) {
            currentPosition++
        }
    }
}
