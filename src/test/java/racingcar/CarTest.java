package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

    private static final int DEFAULT_POSITION = 0;

    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car("monorisk", DEFAULT_POSITION);
    }

    @DisplayName("이름은 null이면 NullPointerException를 발생시킨다")
    @Test
    void constructor_null_name() {
        // given

        // when & then
        // null name
        assertThatThrownBy(() -> new Car(null, DEFAULT_POSITION))
            .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("이름은 5글자 이하라면 IllegalArgumentException을 발생시킨다")
    @Test
    void constructor_short_name() {
        // given

        // when & then
        // short name
        assertThatThrownBy(() -> new Car("1234", DEFAULT_POSITION))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("전달된 전략이 null이라면 NullPointerException를 발생시킨다")
    @Test
    void move_null_strategy() {
        // given

        // when & then
        // null moving strategy
        assertThatThrownBy(() -> car.move(null))
            .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("전달된 전략이 이동할 수 있다면 position을 1증가 시킨다")
    @Test
    void move_forward() {
        // given

        // when
        car.move(new ForwardMovingStrategy());

        // then
        assertThat(car.getPosition()).isEqualTo(DEFAULT_POSITION + 1);
    }

    @DisplayName("전달된 전략이 이동할 수 없다면 position을 그대로 유지한다")
    @Test
    void move_hold() {
        // given

        // when
        car.move(new HoldMovingStrategy());

        // then
        assertThat(car.getPosition()).isEqualTo(DEFAULT_POSITION);
    }
}
