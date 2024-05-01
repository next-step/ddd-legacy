package racingcar

data class Car(val name: String, var position: Int) {

    companion object {
        fun init(name: String): Car {
            return Car(name, 0)
        }
    }
    init {
        require(name.length <= 5) { throw IllegalArgumentException("자동차 이름은 5글자를 초과할 수 없습니다.") }
    }

    fun move(condition: MovingStrategy) {
        if (condition.isMovable()) {
            position++
        }
    }
}