package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class PositiveIntegerTest {

  private PositiveInteger positiveInteger;

  @DisplayName(value = "음수 입력시 에러가 발생한다.")
  @ParameterizedTest
  @CsvSource(value = {"-1"})
  void negative(String value) {
    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> PositiveInteger.getInstance(value));
  }

  @DisplayName(value = "숫자가 아닌 값을 입력시 에러가 발생한다.")
  @ParameterizedTest
  @CsvSource(value = {"a", "11aa", "'//;\n'3#2"})
  void notInteger(String value) {
    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> PositiveInteger.getInstance(value));
  }

  @DisplayName(value = "두 인스턴스를 더하면 새로운 인스턴스가 나온다.")
  @ParameterizedTest
  @CsvSource(value = {"1, 2, 3"})
  void add(String num1, String num2, int result) {
    PositiveInteger first = PositiveInteger.getInstance(num1);
    PositiveInteger second = PositiveInteger.getInstance(num2);
    PositiveInteger addPositive = first.add(second);

    assertThat(addPositive.getNum()).isEqualTo(result);
  }
}
