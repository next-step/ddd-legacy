package racingcar.strategy.number

class RandomGenerateNumberStrategy(
    private val range: IntRange = (MIN_VALUE..MAX_VALUE)
) : GenerateNumberStrategy {
    override fun invoke(): Int {
        return range.random()
    }

    companion object {
        private const val MIN_VALUE = 0
        private const val MAX_VALUE = 9
    }
}
