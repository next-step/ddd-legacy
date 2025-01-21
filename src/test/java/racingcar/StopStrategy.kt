package racingcar

class StopStrategy : MoveStrategy {
    override fun movable(): Boolean {
        return false
    }
}