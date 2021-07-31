package study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {
  @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
  @Test
  void constructorTest() {
    assertThatIllegalArgumentException()
      .isThrownBy(() -> new Car("Hello World"));
  }

  @DisplayName("자동차가 움직인다.")
  @Test
  void moveTest() {
    final Car car = new Car("YJ");
    //car.move(4);
    car.move(new GoStrategy());
    assertThat(car.getPosition()).isEqualTo(1);
  }

  @DisplayName("자동차가 정지한다.")
  @Test
  void notMoveTest() {
    final Car car = new Car("YJ");
    car.move(new StopStrategy());
    assertThat(car.getPosition()).isEqualTo(0);
  }

  /*
  @DisplayName("값이 4 미만인 경우 자동차가 정지한다.")
  @Test
  void randomMoveTest(){
    final Car car = new Car("YJ");
    car.move(new RandomMovingStrategy());
    assertThat(car.getPosition()).isEqualTo(0);
  }
  */
}
