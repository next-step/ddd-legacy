package calculator;

import java.util.List;

public interface Refiner {

    /**
     * @throws IllegalArgumentException {@param text}가 null일 때
     */
    List<String> execute(final String text);
}
