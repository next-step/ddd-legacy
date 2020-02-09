package calculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;

public class Splitter {
    private final Set<Delimiter> delimiters;

    static Builder builder() {
        return new Splitter.Builder();
    }

    Builder toBuilder() {
        return new Builder(new HashSet<>(delimiters));
    }

    private Splitter(Builder builder) {
        this(new HashSet<>(builder.delimiters));
    }

    private Splitter(Set<Delimiter> delimiters) {
        this.delimiters = delimiters;
    }

    public PositiveNumbers split(String text) {
        if (Strings.isBlank(text)) { return PositiveNumbers.EMPTY; }
        return new PositiveNumbers(splitTextsByDelimiters(Collections.singletonList(text),
                                                          delimiters.iterator()).stream()
                                                                                .map(PositiveNumber::from)
                                                                                .collect(Collectors.toList()));
    }

    private static List<String> splitTextsByDelimiters(List<String> texts, Iterator<Delimiter> delimiters) {
        if (!delimiters.hasNext()) {
            return texts;
        }

        Delimiter delimiter = delimiters.next();
        return splitTextsByDelimiters(texts.stream()
                                           .flatMap(t -> Arrays.stream(delimiter.split(t)))
                                           .collect(Collectors.toList()),
                                      delimiters);
    }

    static class Builder {
        private Set<Delimiter> delimiters;

        private Builder() {
            this(new HashSet<>());
        }

        private Builder(Set<Delimiter> delimiters) {
            this.delimiters = new HashSet<>(delimiters);
        }

        public Builder with(Delimiter delimiter) {
            delimiters.add(delimiter);
            return this;
        }

        public Splitter build() {
            return new Splitter(this);
        }
    }
}