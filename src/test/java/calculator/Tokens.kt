package calculator

class Tokens(
    private val tokens: List<Token>
) {
    companion object {

        fun create(delimiters: Array<String>, stringTokens: String): Tokens {
            val splitStringTokens = stringTokens.split(*delimiters).apply { validate() }

            return Tokens(
                tokens = splitStringTokens.map {
                    Token(it.toInt())
                }
            )
        }

        private fun List<String>.validate() {
            require(this.all { it.toIntOrNull() != null }) { "숫자가 아닌 값이 포함되어 있습니다." }
        }
    }

    fun sum(): Int {
        return tokens.sumOf { it.number }
    }
}
