package racingcar

import racingcar.strategy.MovingStrategy

class Car(
    val name: String,
    position: Int = 0
) {
    var position: Int = position
        private set

    init {
        require(name.length <= 5)
    }

    fun move(condition: MovingStrategy) {
        if (condition.movable()) {
            position++
        }
    }
}
