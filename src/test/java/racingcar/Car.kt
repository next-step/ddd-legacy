package racingcar

class Car(
    val name: String,
    var position: Int = 0
) {
    init {
        require(name.length < 5) { "이름은 5자 미만이어야 합니다." }
    }

    fun move(condition: Int) {
        if (condition >= 4) {
            position++
        }
    }
}
