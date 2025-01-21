package racingcar;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {
    @DisplayName("자동차의 이름은 5글자를 초과할 수 없다.")
    @Test
    void constructor() {
        // given
        String carName = "123456";

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Car(carName);

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(throwingCallable);
    }
}
