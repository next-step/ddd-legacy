package calculator

class ParsingService {

    fun parse(input: InputString): List<NonNegativeNumber> {
        val delimiter = Delimiter(input.value)
        return input.target
            .split(delimiter.value)
            .map { NonNegativeNumber(it.toInt()) }
    }
}