package calculator

class Numbers(
    private val numbers: List<Number>,
) {
    companion object {
        fun create(numbers: List<Int>): Numbers {
            return Numbers(
                numbers =
                    numbers.map {
                        Number.create(it)
                    },
            )
        }
    }

    fun sum(): Int {
        return numbers.sumOf { it.number }
    }
}
