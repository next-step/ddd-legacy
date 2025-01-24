package calculator

class Calculator(
    private val parsingService: ParsingService = ParsingService(),
) {

    fun calculate(target: String?): Int =
        InputString.of(target)
            .let { parsingService.parse(it) }
            .sum()
}