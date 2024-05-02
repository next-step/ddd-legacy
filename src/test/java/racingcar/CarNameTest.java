package racingcar;


import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarNameTest {
    @Test
    @DisplayName("자동차 이름은 null 이 될 수 없다")
    void nullTest() {
        // given
        String name = null;

        // when then
        assertThrows(IllegalArgumentException.class, () -> new CarName(name));
    }

    @Test
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다")
    void maxLengthTest() {
        // given
        String name = "abcdef";

        // when then
        assertThrows(IllegalArgumentException.class, () -> new CarName(name));
    }
}
