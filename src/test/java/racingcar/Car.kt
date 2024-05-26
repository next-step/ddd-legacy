package racingcar

data class Car (
    val name: String,
    var position: Int
) {
    init {
        require(name.length <= 5)
        {
            throw IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다.")
        }
    }

    fun move(condition: MovingStrategy) {
        if (condition.isMovable()) {
            position++
        }
    }

    companion object {
        fun init(name: String): Car {
            return Car(name, 0)
        }
    }
}