package racingcar.strategy.move

import racingcar.strategy.number.GenerateNumberStrategy

class RandomMoveStrategy(
    private val generate: GenerateNumberStrategy,
) : MoveStrategy {
    override fun invoke(): Boolean {
        return generate() >= MOVE_STANDARD
    }

    companion object {
        private const val MOVE_STANDARD = 4
    }
}
