package racingcar.domain

import racingcar.strategy.move.MoveStrategy

data class Car(
    val name: Name,
    val position: Position = Position()
) {
    fun moveForward(isMovable: MoveStrategy): Car {
        return if (isMovable()) {
            copy(position = position.forward())
        } else {
            this
        }
    }
}
