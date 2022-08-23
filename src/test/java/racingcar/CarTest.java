package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CarTest {

    @DisplayName("자동차 이름은 5글자를 초과할 수 없다.")
    @Test
    void success() {
        assertThatCode(() -> new Car("gmoon"))
                .doesNotThrowAnyException();
    }

    @DisplayName("자동차 이름은 null 또는 빈 문자열일 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void error1(String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차 이름은 5글자를 초과할 수 없다.")
    @Test
    void error2() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car("123456"));
    }

    @DisplayName("자동차는 움직일 수 있다.")
    @Test
    void move() {
        Car car = new Car("gmoon");
        car.move(MovingStrategy::forward);
        assertThat(car).hasFieldOrPropertyWithValue("position", 1);
    }

    @DisplayName("자동차는 정지할 수 있다.")
    @Test
    void stop() {
        Car car = new Car("gmoon");
        car.move(MovingStrategy::hold);
        assertThat(car).hasFieldOrPropertyWithValue("position", 0);
    }
}