package calculator

interface StringCalculateStrategy {
    fun support(text: String): Boolean

    fun calculate(text: String): Int
}
