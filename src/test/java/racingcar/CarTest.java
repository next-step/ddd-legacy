package racingcar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarTest {

    @Test
    @DisplayName("자동차 이름은 5글자 이하이다")
    void nameTest01() {
        assertDoesNotThrow(() -> {
            new Car("hello");
        });
    }

    @Test
    @DisplayName("자동차 이름이 5글자를 넘으면 IllegalArgumentException 이 발생한다")
    void nameTest02() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Car("hello2");
        });
    }

    @Test
    @DisplayName("자동차 이름은 길이가 0일 수 있다")
    void nameTest03() {
        assertDoesNotThrow(() -> {
            new Car("");
        });
    }

    @Test
    @DisplayName("자동차 이름은 null 이면 NullPointerException 이 발생한다")
    void nameTest04() {
        assertThrows(NullPointerException.class, () -> {
            new Car(null);
        });
    }
}
