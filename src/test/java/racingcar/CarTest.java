package racingcar;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

  @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
  @Test
  void nameMustBeLessThan5Letters() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new Car("동해물과 백두산이"));
  }

  @DisplayName("숫자가 4 이상인 경우 자동차가 움직인다.")
  @Test
  void canMoveWhenPositionIs4OrUpper() {
    final var car = new Car("추교준");
    car.move(4);
    assertThat(car.position()).isEqualTo(1);
  }

  @DisplayName("숫자가 4 미만인 경우 자동차는 움직이지 않는다.")
  @Test
  void canNotMoveWhenPositionLessThan4() {
    final var car = new Car("추교준");
    car.move(3);
    assertThat(car.position()).isEqualTo(0);
  }
}
