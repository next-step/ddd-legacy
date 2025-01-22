package racingcar

class Car(
    private val name: String = ""
) {
    var position: Int = 0
        private set

    init {
        require(name.length <= 5) { "이름은 5글자를 초과할 수 없습니다" }
    }

    fun move(condition: Int) {
        move(ConditionalStrategy(condition))
    }

    fun move(moveStrategy: MoveStrategy) {
        if (moveStrategy.movable()) {
            position++
        }
    }
}
