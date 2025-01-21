package racingcar

class Car(val name: String) {

    var position: Int = 0

    init {
        require(name.length <= 5)
    }

    fun move(condition: Int) {
        require(condition in (0..9))
        move { condition >= 4 }
    }

    fun move(condition: () -> Boolean) {
        if (condition()) {
            position++
        }
    }

    fun move(movingStrategy: MovingStrategy) {
        if (movingStrategy.canMove()) {
            position++
        }
    }
}

fun interface MovingStrategy {

    fun canMove(): Boolean
}
