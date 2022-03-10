package calculator;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

final class Delimiter {

    static final Delimiter COMMA;
    static final Delimiter COLON;

    private static final Set<String> RESERVED_WORDS;
    private static final Pattern NUMBER_PATTERN;

    static {
        final Set<String> reservedWords;
        reservedWords = new HashSet<>();
        reservedWords.add("<");
        reservedWords.add("(");
        reservedWords.add("[");
        reservedWords.add("{");
        reservedWords.add("\\");
        reservedWords.add("^");
        reservedWords.add("-");
        reservedWords.add("=");
        reservedWords.add("$");
        reservedWords.add("!");
        reservedWords.add("|");
        reservedWords.add("]");
        reservedWords.add("}");
        reservedWords.add(")");
        reservedWords.add("?");
        reservedWords.add("+");
        reservedWords.add(".");
        reservedWords.add(">");
        RESERVED_WORDS = Collections.unmodifiableSet(reservedWords);

        NUMBER_PATTERN = Pattern.compile("[0-9]");

        COMMA = new Delimiter(",");
        COLON = new Delimiter(":");
    }

    private final String value;

    Delimiter(final String value) {
        validateValue(value);

        this.value = RESERVED_WORDS.contains(value) ? "\\Q" + value + "\\E" : value;
    }

    private void validateValue(final String value) {
        requireNonNull(value);

        if (value.length() != 1) {
            throw new IllegalArgumentException("value's length must be 1.");
        }

        if (NUMBER_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("number is not allowed.");
        }
    }

    String getValue() {
        return value;
    }
}
