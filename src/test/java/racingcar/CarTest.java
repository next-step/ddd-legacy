package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class CarTest {

    @DisplayName("자동차를 생성한다.")
    @Test
    void create() {
        final Car car = new Car("jason");
        assertThat(car).isNotNull();
    }

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"kkiluk", "kkkkiluk"})
    void create(final String name) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void move() {
        final Car car = new Car("car", 1);
        car.move(new TestMovingStrategy());
        assertThat(car.getPosition()).isEqualTo(2);

        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(3);
    }
}
