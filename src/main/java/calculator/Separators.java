package calculator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class Separators {
    private final Set<String> separators = new HashSet<>();

    private Separators(Collection<String> separators) {
        if (Objects.isNull(separators) || separators.isEmpty()) {
            throw new IllegalArgumentException("구분자는 필수 입니다");
        }
        this.separators.addAll(separators);
    }

    public static Separators of(Collection<String> separators) {
        return new Separators(separators);
    }

    public List<Integer> separate(String text) {
        String regex = String.join("|", separators);
        return Stream.of(text.split(regex))
            .map(this::extractInteger)
            .toList();
    }


    public void addSeparator(String separator) {
        this.separators.add(separator);
    }

    private int extractInteger(String splitText) {
        int element;
        try {
            element = Integer.parseInt(splitText);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아닙니다.");
        }
        return element;
    }
}
