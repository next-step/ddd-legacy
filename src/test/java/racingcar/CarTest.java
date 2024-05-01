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
  @DisplayName("무작위_값_4이상_인_경우에만_자동차가_움직인다")
  void 무작위_값_4이상_인_경우에만_자동차가_움직인다() {
    final int condition = new Random().nextInt(10);

    final Car car = new Car("움직인다", 0);
    car.setPosition(condition);

    if (car.getPosition() > car.getCurrentPosition()) {
      Assertions.assertThat(car.isMoving()).isTrue();

    } else {
      Assertions.assertThat(car.isMoving()).isFalse();
    }
  }
}
