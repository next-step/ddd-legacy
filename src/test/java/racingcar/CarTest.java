package racingcar;

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
  void name() {}
}
