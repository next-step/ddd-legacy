package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;


class CarTest {
    @DisplayName("자동차 이름은 5자 이하여야 합니다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("자동차이름다섯글자"));
    }
}