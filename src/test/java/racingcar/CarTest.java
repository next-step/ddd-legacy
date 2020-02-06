package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class CarTest {
    @DisplayName("자동차를 생성한다.")
    @Test
    void test_create_car() {
        final Car car = new Car("alice");
        assertThat(car).isNotNull();

    }

    @DisplayName("자동차 이름은 5글자 넘어가면 안된다.")
    @ParameterizedTest
    @ValueSource(strings = {"alice!", "TEST!!!"})
    void test_car_name(String name) {

        assertThatThrownBy(() -> {
            new Car(name);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() ->
                        new Car(name));

    }


    @DisplayName("자동차가 움직인다.")
    @Test
    void test_car_is_moved() {

        final Car car = new Car("alice");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);

    }


    @DisplayName("자동차가 움직이지 않는다.")
    @Test
    void test_car_is_not_moved() {

        final Car car = new Car("alice");
        car.move(() -> false);
        assertThat(car.getPosition()).isEqualTo(0);

    }

}