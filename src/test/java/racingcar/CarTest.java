package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName("값이 4 이상일 경우 자동차는 전진한다")
    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    void move(int input) {
        final var car = new Car("강지우");
        car.move(() -> input >= 4);
        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("값이 4 미만일 경우 자동차는 움직이지 않는다")
    @ParameterizedTest
    @ValueSource(ints = {0 , 1, 2, 3})
    void stop(int input) {
        final var car = new Car("강지우");
        car.move(() -> input >= 4);
        assertThat(car.position()).isEqualTo(0);
    }

}
