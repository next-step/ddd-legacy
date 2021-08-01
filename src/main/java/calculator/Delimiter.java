package calculator;

@FunctionalInterface
public interface Delimiter {
    String[] parse(String string);
}
