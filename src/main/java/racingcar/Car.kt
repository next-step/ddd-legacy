package racingcar

class Car(
    val name: String
) {
    var position: Int = 0

    init {
        require(name.length > 5) { throw IllegalArgumentException() }
    }

    fun move(condition: Int) {
        if(condition >= 4) {
            position++
        }
    }
}
