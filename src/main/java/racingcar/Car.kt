package racingcar

class Car(
    val name: String,
) {
    init {
        require(name.length > 5) { throw IllegalArgumentException() }
    }
}
