package calculator

class EmptyTextCalculator : StringCalculateStrategy {
    override fun support(text: String?): Boolean =
        text.isNullOrEmpty()

    override fun calculate(text: String?): Int = 0
}