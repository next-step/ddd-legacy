package racingcar

class Car(
    val name: String,
    position: Int = 0
) {
    var position: Int = position
        private set

    init {
        require(name.length <= 5)
    }

    fun move(condition: Int) {
        if (condition >= 4) {
            position++
        }
    }
}
