package calculator

class Numbers(
    private val numbers: List<Number>
) {
    companion object {

        fun create(delimiters: Array<String>, stringTokens: String): Numbers {
            val splitStringTokens = stringTokens.split(*delimiters).apply { validate() }

            return Numbers(
                numbers = splitStringTokens.map {
                    Number(it.toInt())
                }
            )
        }

        private fun List<String>.validate() {
            require(this.all { it.toIntOrNull() != null }) { "숫자가 아닌 값이 포함되어 있습니다." }
        }
    }

    fun sum(): Int {
        return numbers.sumOf { it.number }
    }
}
