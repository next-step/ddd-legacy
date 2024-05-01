package racingcar

class Car(
    val name: String,
) {
    var position: Int = 0
        private set

    init {
        require(name.length <= 5) { "자동차 이름은 5글자를 넘을 수 없습니다." }
    }

    fun move(movingStrategy: MovingStrategy) {
        if (movingStrategy.isMovable()) {
            position += 1
        }
    }
}
