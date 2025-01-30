package racingcar

class Car(
    val name: String,
) {
    var position: Int = 0

    init {
        require(name.length > 5) { throw IllegalArgumentException("자동차의 이름은 5글자를 초과하면 안됩니다.") }
    }

    fun move(movingStartegy: MovingStrategy) {
        if (movingStartegy.movable()) {
            position++
        }
    }
}
