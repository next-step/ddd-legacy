package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import racingcar.strategy.StopStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {

  @DisplayName("자동차 이름의 글자 수를 제한한다.")
  @Test
  void constructor() {
    assertThatIllegalArgumentException()
            .isThrownBy(() -> Car.createCar("기준치를넘는이름글자수", 0))
            .withMessageStartingWith("이름의 글자수가");

  }

  @DisplayName("조건 숫자가 정해진 숫자 이상인 경우 자동차는 전진한다.")
  @Test
  void move() {
    Car car = Car.createCar("Simon", 0);

    car.move(() -> true);

    assertThat(car.getPosition()).isEqualTo(1);
  }

  @DisplayName("조건 숫자가 정해진 숫자 미만인 경우 자동차는 움직이지 않는다.")
  @Test
  void stop() {
    Car car = Car.createCar("Simon", 0);

    car.move(new StopStrategy());

    assertThat(car.getPosition()).isEqualTo(0);
  }
}
