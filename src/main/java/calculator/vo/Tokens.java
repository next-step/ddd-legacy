package calculator.vo;

import java.util.Arrays;
import java.util.List;

public record Tokens(List<String> token) {

    public Tokens(String[] tokens) {
        this(Arrays.stream(tokens)
            .map(String::trim)
            .filter(token -> !token.isEmpty())
            .toList());
    }

    public boolean isEmpty() {
        return token.isEmpty();
    }

    public int size() {
        return token.size();
    }

    @Override
    public String toString() {
        return token.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tokens that)) return false;
        return token.equals(that.token);
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }
}
