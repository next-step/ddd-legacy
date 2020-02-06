package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CarTest {
    @DisplayName("자동차를 생성한다.")
    @Test
    void create() {
        final Car car = new Car("BMW");
        assertThat(car).isNotNull();
    }

    @DisplayName("자동차의 이름은 다섯 글자를 넘을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"BMX X1", "SONATA"})
    void exceededName(String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차의 이름은 빈 값일 수 없다.")
    @Test
    void emptyName() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(""));
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void validateCarMoving() {
        final Car car = new Car("BMW");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }
}
