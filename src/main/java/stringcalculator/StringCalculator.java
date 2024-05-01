package stringcalculator;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private final Numbers numbers;

    public StringCalculator(String userInput) {
        this.numbers = new Numbers(userInput);
    }

    public int calculate() {
        return numbers.sum();
    }
}
