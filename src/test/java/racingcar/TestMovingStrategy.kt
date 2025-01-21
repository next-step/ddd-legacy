package racingcar

class ForwardStrategy : MovingStrategy {

    override fun canMove(): Boolean = true
}

class StopStrategy : MovingStrategy {

    override fun canMove(): Boolean = false
}
