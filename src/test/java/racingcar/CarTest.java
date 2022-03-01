package racingcar;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CarTest {

    @Test
    void 자동차_이름은_5글자를_넘을수_없다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("abcdef", 5));
    }

    @Test
    void 자동가_전진한다() {
        Car car = new Car("abcde", 0);

        car.move(new GoMovingStrategy());

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    void 자동가_전진하지_않는다() {
        Car car = new Car("abcde", 0);

        car.move(new StopMovingStrategy());

        assertThat(car.getPosition()).isEqualTo(0);
    }
}
