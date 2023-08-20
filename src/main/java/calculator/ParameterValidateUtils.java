package calculator;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 해당 클래스는 순수한 파라미터 검사를 위해 만들어진 클래스이므로, 비즈니스 종속적인 곳에서는 사용하면 안된다.
 */
public final class ParameterValidateUtils {

    static <T> T checkNotNull(final T value, final String name) {
        checkArgument(value != null, "null %s", name);

        return value;
    }


    static <E> List<E> checkNotEmpty(final List<E> collection, final String name) {
        checkArgument(CollectionUtils.isNotEmpty(collection), "%s is empty", name);

        return collection;
    }

    private ParameterValidateUtils() {
        throw new UnsupportedOperationException();
    }
}
