package racingcar;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarTest {

    @Test
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    void throwsExceptionWhenCreateCar() {
        assertThatThrownBy(() -> {
            Car car = new Car("가나다라마바");
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
