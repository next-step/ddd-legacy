package racingcar

class StopStrategy : MovingStrategy {
    override fun isMovable(): Boolean {
        return false
    }
}