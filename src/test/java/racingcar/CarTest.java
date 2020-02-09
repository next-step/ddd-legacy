package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CarTest {
    @DisplayName("자동차를 생성한다.")  // space, dot 등을 사용할 수 있다
    @Test
    void create() {
        final Car car = new Car("jason");   // test scope 에서는 Junit이 필요없는 객체라도, 코드라도 모두 수행해준다
        assertThat(car).isNotNull();
    }

    @DisplayName("자동차의 이름은 5 글자를 넘을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"동해물과 백두산이", "마르고 닮도록"})
    void checkLimitOfNameLength(final String name) {
        Assertions.assertThatThrownBy(() -> {
            final Car car = new Car(name);
        }).isInstanceOf(IllegalArgumentException.class);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void move() {
        final Car car = new Car("jason");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }

}
