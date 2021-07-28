package calculator;

import java.util.List;

@FunctionalInterface
public interface Separator {
    List<Integer> separate(String text);
}
