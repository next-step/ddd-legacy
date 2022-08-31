package calculator;

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
}
