package calculator

class Numbers(
    private val numbers: List<Number>
) {
    companion object {

        fun create(splitStringNumbers: List<String>): Numbers {
            return Numbers(
                numbers = splitStringNumbers.map {
                    Number.create(it)
                }
            )
        }
    }

    fun sum(): Int {
        return numbers.sumOf { it.number }
    }
}
