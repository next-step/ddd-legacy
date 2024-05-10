package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NumberTest {

  @DisplayName(value = "정수가 아닌 문자열을 전달할 경우 NumberFormatException 예외 처리를 한다.")
  @ParameterizedTest
  @ValueSource(strings = {"a", "1.08", "33ks", "", "   "})
  void createString(final String number) {
    assertThatExceptionOfType(NumberFormatException.class)
        .isThrownBy(() -> Number.createPositive(number));
  }

  @DisplayName(value = "음수인 정수를 전달할 경우 NumberFormatException 예외 처리를 한다.")
  @ParameterizedTest
  @ValueSource(ints = {-3, -7, -2324})
  void createNegative(final int number) {
    assertThatExceptionOfType(NumberFormatException.class)
        .isThrownBy(() -> Number.createPositive(number));
  }

  @DisplayName(value = "숫자 두개의 합을 반환한다.")
  @Test
  void add() {
    Number five = Number.createPositive(5);
    Number twentyFive = Number.createPositive(25);
    assertThat(five.add(twentyFive)).isEqualTo(Number.createPositive(30));
  }
}
