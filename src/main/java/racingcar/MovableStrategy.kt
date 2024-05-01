package racingcar

class MovableStrategy(
    private val condition: Int,
) : MovingStrategy {
    override fun movable(): Boolean {
        return condition >= 4
    }
}
