package kitchenpos.test;

import java.util.List;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.assertj.core.api.ThrowableTypeAssert;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.BDDMockito.BDDMyOngoingStubbing;
import org.mockito.BDDMockito.Then;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.opentest4j.MultipleFailuresError;
import org.opentest4j.TestAbortedException;

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

    public AbstractThrowableAssert<?, ? extends Throwable> assertThatThrownBy(
            ThrowingCallable shouldRaiseThrowable) {
        return Assertions.assertThatThrownBy(shouldRaiseThrowable);
    }

    public <T> BDDMyOngoingStubbing<T> given(T methodCall) {
        return BDDMockito.given(methodCall);
    }

    public <T> OngoingStubbing<T> when(T methodCall) {
        return Mockito.when(methodCall);
    }

    public <T> Then<T> then(T mock) {
        return BDDMockito.then(mock);
    }

    public <T> T any() {
        return ArgumentMatchers.any();
    }

    public void assumeTrue(boolean assumption) throws TestAbortedException {
        Assumptions.assumeTrue(assumption);
    }

    public void assertAll(Executable... executables) throws MultipleFailuresError {
        org.junit.jupiter.api.Assertions.assertAll(executables);
    }
}
