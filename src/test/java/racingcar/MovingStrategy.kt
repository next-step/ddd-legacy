package racingcar

@FunctionalInterface
interface MovingStrategy {
    fun movable(): Boolean
}