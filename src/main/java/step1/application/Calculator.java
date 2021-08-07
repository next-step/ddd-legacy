package step1.application;

import step1.domain.Number;

import java.util.List;

public interface Calculator {
    List<String> parseOperators(String expression);

    List<Number> parseNumbers(String expression);

    int sum(String expression);
}
