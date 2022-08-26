package calculate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class StringCalculatorTest {

  @DisplayName("비어 있는 문자열을 입력할 경우 0을 반환한다.")
  @ParameterizedTest
  @NullAndEmptySource
  void calculate_NotNullAndNotEmpty(String text) {
    assertThat(StringCalculator.calculate(text)).isZero();
  }

}