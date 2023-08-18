package calculator;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.commons.lang3.StringUtils;

public final class ValidateUtils {


    static String checkNotEmpty(final String value, final String name) {
        checkArgument(StringUtils.isNotEmpty(value), "empty %s", name);

        return value;
    }

    static <T> T checkNotNull(final T value, final String name) {
        checkArgument(value != null, "null %s", name);

        return value;
    }


    private ValidateUtils() {
        throw new UnsupportedOperationException();
    }
}
