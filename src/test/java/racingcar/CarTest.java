package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

    private static final int POSITION_ZERO = 0;

    @DisplayName("자동차의 이름이 5글자가 넘으면 IllegalArgumentException 을 던진다.")
    @Test
    public void construct_when_name_is_over_5() {
        assertThatThrownBy(() -> new Car("다섯글자넘음"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차의 이름이 null 이면 IllegalArgumentException 을 던진다.")
    @Test
    public void construct_when_name_is_null() {
        assertThatThrownBy(() -> new Car(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 움직이는 경우, 위치가 1 증가한다.")
    @Test
    public void move_when_movingStrategy_is_movable() {
        Car car = carWith(POSITION_ZERO);

        car.move(new RandomMovingStrategy() {
            @Override
            boolean movable() {
                return true;
            }
        });

        assertThat(car.getPosition())
            .isEqualTo(POSITION_ZERO + Car.POINT_VARIATION_OF_MOVING);
    }

    @DisplayName("자동차가 움직이는 경우에만 위치가 1 증가한다.")
    @Test
    public void move_when_movingStrategy_is_not_movable() {
        Car car = carWith(POSITION_ZERO);

        car.move(new RandomMovingStrategy() {
            @Override
            boolean movable() {
                return false;
            }
        });

        assertThat(car.getPosition()).isEqualTo(POSITION_ZERO);
    }

    private static Car carWith(int position) {
        return new Car("Car", position);
    }
}