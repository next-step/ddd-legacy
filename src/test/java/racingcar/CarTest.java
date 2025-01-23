package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CarTest {

    @DisplayName("자동차의 이름은 5글자를 초과하면 예외가 발생한다")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName("이동 조건의 값이 4 이상이면 자동차는 움직인다")
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    @ParameterizedTest()
    void move(final int condition) {
        final var car = new Car("자동차이름");

        car.move(() -> condition >= 4);

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("이동 조건의 값이 4 미만이면 자동차는 정지한다")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest()
    void stop(final int condition) {
        final var car = new Car("자동차이름");

        car.move(() -> condition >= 4);

        assertThat(car.getPosition()).isEqualTo(0);
    }

    @DisplayName("이동조건 전략이 true면 자동차는 이동한다")
    @Test
    void move() {
        final var car = new Car("자동차이름");

        car.move(new ForwardStrategy());

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("이동조건 전략이 false면 자동차는 정지한다")
    @Test
    void stop() {
        final var car = new Car("자동차이름");

        car.move(new StopStrategy());

        assertThat(car.getPosition()).isEqualTo(0);
    }
}
