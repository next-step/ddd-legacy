package racingcar;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CarTest {

    @DisplayName("자동차를 생성한다.")
    @Test
    void name() {
        final Car car = new Car("jby");
        assertThat(car).isNotNull();
    }

    @DisplayName("자동차의 이름은 5글자를 넘을 수 없다.")
    @Test
    void nameLength() {
        assertThatThrownBy(() -> new Car("helloworld"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차의 이름은 5글자를 넘을 수 없다 2.")
    @ParameterizedTest
    @ValueSource(strings = {"동해물과백두", "산이마르고닳"})
    void nameLength2(final String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차는 4이상의 값에서만 움직인다.")
    @Test
    void move() {
        final Car car = new Car("hello");

        car.move(() -> false);
        assertThat(car.getPosition()).isEqualTo(0);

        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }
}
