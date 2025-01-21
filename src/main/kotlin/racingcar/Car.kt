package racingcar

class Car(name: String) {

    private val _name: String

    init {
        require(name.length <= 5)
        this._name = name
    }
}