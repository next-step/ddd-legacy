package calculator.domain;

public class InputParser {
    public String[] parse(String input) {
        if (input.startsWith("//")) {
            String[] tokens = input.split("\n", 2);
            String customDelimiter = tokens[0].substring(2);
            return tokens[1].split(customDelimiter);
        }

        return input.split(",|:");
    }
}

