package racingcar;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;

class CarTest {

    @Test
    @DisplayName("자동차 이름은 5자를 넘길수 없다.")
    void constructor(){
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("nameiscar"));
    }

    @DisplayName("숫자가 4 이상이면 자동차가 움직인다")
    @ValueSource(ints = {4,5,6,7,8,9})
    @ParameterizedTest
    void move(final int condition) {
        Car car = new Car("bab2");
        car.move(4);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만이면 자동차가 정지한다")
    @ValueSource(ints = {0,1,2,3})
    @ParameterizedTest
    void stop(final int condition) {
        Car car = new Car("bab2");
        car.move(3);
        assertThat(car.getPosition()).isEqualTo(0);
    }
}