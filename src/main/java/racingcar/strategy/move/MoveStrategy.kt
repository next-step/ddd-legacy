package racingcar.strategy.move

fun interface MoveStrategy {
    operator fun invoke(): Boolean
}
