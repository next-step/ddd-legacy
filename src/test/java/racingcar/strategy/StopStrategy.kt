package racingcar.strategy

class StopStrategy : MovingStrategy {
    override fun movable(): Boolean = false
}