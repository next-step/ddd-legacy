package racingcar

class StopStrategy : MovingStrategy {
    override fun movable(): Boolean = false
}