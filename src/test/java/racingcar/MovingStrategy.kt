package racingcar

sealed interface MovingStrategy {
    fun isMovable(): Boolean

    data object Forward : MovingStrategy {
        override fun isMovable(): Boolean = true
    }

    data object Stop : MovingStrategy {
        override fun isMovable(): Boolean = false
    }
}
