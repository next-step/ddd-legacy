package calculator;

public interface Refiner {

    /**
     * @throws IllegalArgumentException {@param text}가 null일 때
     */
    Numbers execute(final String text);
}
