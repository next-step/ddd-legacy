package racingcar

class Car(name: String) {
    private var name: String = ""
    private var _position: Int = 0

    val position get() = _position

    init {
        require(name.length <= 5) { "이름은 5글자를 초과할 수 없습니다" }
        this.name = name
    }

    fun move(condition: Int) {
        move { condition > 4 }
    }

    fun move(condition: MoveStrategy) {
        if (condition.movable()) {
            _position++
        }
    }

}