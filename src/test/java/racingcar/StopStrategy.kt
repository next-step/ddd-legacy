package racingcar

class StopStrategy: MovingStrategy {
    override fun movable(): Boolean {
        return false;
    }
}
