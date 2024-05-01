package racingcar;

import java.util.Random;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

  @Test
  @DisplayName("자동차_이름은_5글자를_넘을_수_없다")
  void 자동차_이름은_5글자를_넘을_수_없다() {
    Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> new Car("동해물과백두산이", 0));
  }

  @Test
  @DisplayName("값_4이상일_경우")
  void 값_4이상일_경우() {
    final Car car = new Car("움직인다", 0);
    MovingStrategy movingStrategy = new GoStrategy();
    Assertions.assertThat(
            car.isMoving(movingStrategy, 5)).isTrue();
  }

  @Test
  @DisplayName("값_4이하일_경우")
  void 값_4이하일_경우() {
    final Car car = new Car("넌안된다", 0);
    MovingStrategy movingStrategy = new StopStrategy();
    Assertions.assertThat(
            car.isMoving(movingStrategy, 3)).isFalse();
  }
}
