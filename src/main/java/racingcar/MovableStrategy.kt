package racingcar

class MovableStrategy(
    private val condition: Int,
) : MovingStrategy {
    override fun movable(): Boolean {
        return (condition in 0..9) && (condition >= 4)
    }
}
