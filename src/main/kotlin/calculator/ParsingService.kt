package calculator

class ParsingService {

    fun parse(input: InputString): List<NonNegativeNumber> {
        val delimiter = Delimiter(input)
        return input.target
            .split(delimiter.regex)
            .map { NonNegativeNumber(it.toInt()) }
    }
}