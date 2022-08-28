package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import racingcar.source.Car;
import racingcar.source.CarName;
import racingcar.source.strategy.ForwardMoveStrategy;
import racingcar.source.strategy.HoldMoveStrategy;

import static org.assertj.core.api.Assertions.assertThat;

public class CarTest {
    @DisplayName("자동차가 움직인다.")
    @Test
    void move(){
        final Car car = new Car(new CarName("myCar"), 0);
        car.move(new ForwardMoveStrategy());
        assertThat(car.getPosition()).isEqualTo(1);

    }

    @DisplayName("자동차가 움직이지 않는다.")
    @Test
    void stop(){
        final Car car = new Car(new CarName("myCar"), 0);
        car.move(new HoldMoveStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }



}
