package study1;

@FunctionalInterface
public interface SplitStrategy {

    String[] split(String text);
}
