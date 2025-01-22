package racingcar

class ConditionalStrategy(private val condition: Int) : MoveStrategy {
    override fun movable(): Boolean {
        return condition > 4
    }
}
