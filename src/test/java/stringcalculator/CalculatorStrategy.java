package stringcalculator;

public interface CalculatorStrategy {
    boolean isCustom(String input);
    String getSeparator(String input);
}
