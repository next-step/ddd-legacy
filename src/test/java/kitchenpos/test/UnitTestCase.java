package kitchenpos.test;

import java.util.List;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.ThrowableTypeAssert;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.BDDMockito.BDDMyOngoingStubbing;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class UnitTestCase {

    public <T> ObjectAssert<T> assertThat(T actual) {
        return Assertions.assertThat(actual);
    }

    public <ELEMENT> ListAssert<ELEMENT> assertThat(List<? extends ELEMENT> actual) {
        return Assertions.assertThat(actual);
    }

    public AbstractThrowableAssert<?, ? extends Throwable> assertThatCode(
            ThrowingCallable shouldRaiseOrNotThrowable) {
        return Assertions.assertThatCode(shouldRaiseOrNotThrowable);
    }

    public ThrowableTypeAssert<IllegalArgumentException> assertThatIllegalArgumentException() {
        return Assertions.assertThatIllegalArgumentException();
    }

    public <T> BDDMyOngoingStubbing<T> given(T methodCall) {
        return BDDMockito.given(methodCall);
    }

    public <T> T any() {
        return ArgumentMatchers.any();
    }
}
