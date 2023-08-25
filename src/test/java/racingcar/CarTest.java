package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CarTest {

  @DisplayName("Car는 이름을 갖는다")
  @Test
  void name() {
    final Car car = new Car("Tesla");
    assertThat(car.getName()).isEqualTo("Tesla");
  }

  @DisplayName("Car의 이름은 5글자를 넘어갈 수 없다")
  @Test
  void longName() {
    assertThatThrownBy(() -> new Car("Teslaaaaa"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("자동차의 이름은 5글자를 넘을 수 없습니다");
  }

  @DisplayName("Car는 무작위 값이 4 이상인 경우 전진한다")
  @ValueSource(ints = {4, 5, 6, 7, 8, 9})
  @ParameterizedTest
  void move(final int condition) {
    final Car car = new Car("Tesla");
    car.move(new NumberMoveCondition(condition));
    assertThat(car.getPosition()).isEqualTo(1);
  }

  @DisplayName("Car는 정지한다")
  @Test
  void stop() {
    final Car car = new Car("Tesla");
    car.move(() -> false);
    assertThat(car.getPosition()).isZero();
  }

  @DisplayName("Car는 무작위 값이 3 이하인 경우 정지한다")
  @ValueSource(ints = {0, 1, 2, 3})
  @ParameterizedTest
  void stop(final int condition) {
    final Car car = new Car("Tesla");
    car.move(new NumberMoveCondition(condition));
    assertThat(car.getPosition()).isZero();
  }
}
