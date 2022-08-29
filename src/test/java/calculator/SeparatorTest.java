package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SeparatorTest {

  private Separator separator;

  @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정하고, 커스텀 구분자로 구분된 숫자를 구할 수 있다.")
  @ParameterizedTest
  @CsvSource(value = {"'//;\n1;2;3'"})
  void customDelimiterAnd(final String text) {
    separator = new Separator(text);

    assertThat(separator.numbers()).containsExactly("1", "2", "3");
  }
}
