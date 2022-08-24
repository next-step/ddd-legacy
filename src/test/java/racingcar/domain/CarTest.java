package racingcar.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import racingcar.strategy.ForwardStrategy;
import racingcar.strategy.HoldStrategy;

class CarTest {

    private static final String VALID_NAME = "valid";

    @DisplayName("이름은 비어있을수 없다")
    @ParameterizedTest
    @NullAndEmptySource
    void createWithNullAndEmptyName(String nameOfCar) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car(nameOfCar));
    }

    @DisplayName("이름이 기준값 보다 클 경우 예외 발생")
    @ParameterizedTest
    @ValueSource(strings = {"aaaaaa", "invalidName"})
    void createWithInvalidCarName(String nameOfCar) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car(nameOfCar));
    }

    @DisplayName("초기 거리값 검증")
    @Test
    void validateInitialDistance() {
        // given
        Car car = new Car(VALID_NAME);

        // when
        int distance = car.getDistance();

        // then
        assertThat(distance).isZero();
    }

    @DisplayName("움직이는 전략일 경우 거리 증가")
    @Test
    void movable() {
        // given
        Car car = new Car(VALID_NAME);

        // when
        car.move(new ForwardStrategy());

        // then
        assertThat(car.getDistance()).isOne();
    }

    @DisplayName("움직이지 않는 전략일 경우 거리 고정")
    @Test
    void immovable() {
        // given
        Car car = new Car(VALID_NAME);

        // when
        car.move(new HoldStrategy());

        // then
        assertThat(car.getDistance()).isZero();
    }
}
