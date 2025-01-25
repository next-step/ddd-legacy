package calculator.domain;

import java.util.List;

public interface InputParser {
    List<String> parse(String input);
}