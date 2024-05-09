package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PositiveNumberTest {
  @DisplayName(value = "음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
  @Test
  void negative() {
    assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> PositiveNumber.from("-1"));
  }

}
