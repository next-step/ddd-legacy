package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CarTest {

    @Test
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    void createCar() {
        // given
        String name = "abcdef";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> new Car(name));
    }

}
