package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CarTest {
    @Test
    @DisplayName("자동차를 생성한다.")
    void create() {
        final Car car = new Car("sju");
        assertThat(car).isNotNull();
    }

    @Test
    @DisplayName("자동차 이름 예외처리")
    void validateByNameLength() {
        Car car = new Car("seong");
        int carNameLength = car.getName()
                .length();
        assertThat(carNameLength).isEqualTo(5);

        assertThatIllegalArgumentException().isThrownBy(() -> {
            new Car("seongju");
        });
    }

    @Test
    @DisplayName("자동차 이동 유무 처리")
    void move() {
        Car car = new Car("sj", 0);

        car.move(() -> false);
        assertThat(car.getPosition()).isEqualTo(0);

        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);

    }
}