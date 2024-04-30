package racingcar

data class Car(val name: String) {
    init {
        if (name.length > 5) {
            throw IllegalArgumentException()
        }
    }
}
