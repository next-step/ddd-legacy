package racingcar

import java.lang.IllegalArgumentException

data class Car(
    val name: String,
) {
    var position: Int = 0

    init {
        if (name.length > 5) {
            throw IllegalArgumentException("can't limit car's name 5")
        }
    }

    fun move(randomNumber: Int, moveCondition: (Int) -> Boolean) {
        if (moveCondition(randomNumber)) {
            position++
        }
    }
}