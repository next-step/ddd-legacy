package stringcaculator

data class Numbers(val nums: List<Number>) {

    companion object {
        fun generateNumbers(text: String, delimiters: Array<String>): Numbers {
            return Numbers(
                text.split(*delimiters)
                    .map { Number(it) }
            )
        }
    }

    fun sum(): Int = nums.sumOf { it.num.toInt() }
}