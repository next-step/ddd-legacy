package calculator;

import java.util.Objects;

import org.apache.logging.log4j.util.Strings;

public class Delimiter {
    public final String val;

    public Delimiter(String delimiter) {
        if (Strings.isEmpty(delimiter)) { throw new IllegalArgumentException(); }
        this.val = delimiter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Delimiter delimiter1 = (Delimiter) o;
        return val.equals(delimiter1.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }

    public String[] split(String text) {
        return text.split(val);
    }
}
