package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumbersTest {

  @DisplayName(value = "숫자들의 합을 반환한다.")
  @Test
  void sum() {
    Numbers numbers = Numbers.create(new String[]{"1", "2", "3", "4", "5"});
    assertThat(numbers.sum()).isEqualTo(Number.createPositive(15));
  }
}
