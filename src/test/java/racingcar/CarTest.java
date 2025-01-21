package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @DisplayName("자동차의 이름은 5글자를 초과하면 예외가 발생한다.")
    @Test
    void constructor() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> new Car("gugbab2"));
    }

}