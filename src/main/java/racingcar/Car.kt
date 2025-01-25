package racingcar

class Car(
    val name: String,
) {
    var position: Int = 0

    init {
        require(name.length > 5) { throw IllegalArgumentException() }
    }

    fun move(movingStartegy: MovingStrategy) {
        if (movingStartegy.movable()) {
            position++
        }
    }
}
