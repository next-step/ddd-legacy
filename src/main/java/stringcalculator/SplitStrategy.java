package stringcalculator;


public interface SplitStrategy {
    String[] split(String text);
    boolean canSplit(String text);
}
