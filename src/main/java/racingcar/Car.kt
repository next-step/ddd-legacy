package racingcar

data class Car(val name: String) {
    var currentPosition = 0
        private set

    init {
        require(name.length <= 5) {
            "name 은 5 글자를 넘어갈수 없습니다."
        }
    }

    fun move(condition: Int) {
        require(condition in 0..9) {
            "condition 은 0 ~ 9 사이의 값만 가능합니다."
        }

        val strategy = MovableStrategy(condition = condition)
        changePositionBy(strategy = strategy)
    }

    private fun changePositionBy(strategy: MovingStrategy) {
        if (strategy.movable()) {
            currentPosition++
        }
    }
}
