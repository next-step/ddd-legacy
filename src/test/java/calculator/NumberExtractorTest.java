package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class NumberExtractorTest {
  private NumberExtractor extractor;

  @BeforeEach
  void setUp() {
    extractor = new NumberExtractor();
  }

  @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자가 한개만 있는 리스트를 반환한다.")
  @ParameterizedTest
  @ValueSource(strings = {"1"})
  void oneNumber(final String text) {
    assertThat(extractor.extract(text))
            .containsExactly(PositiveNumber.from(text));
  }

  @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 리스트를 반환한다.")
  @ParameterizedTest
  @ValueSource(strings = {"1,2"})
  void twoNumbers(final String text) {
    assertThat(extractor.extract(text))
            .containsExactly(PositiveNumber.from("1"), PositiveNumber.from("2"));
  }

  @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
  @ParameterizedTest
  @ValueSource(strings = {"1,2:3"})
  void colons(final String text) {
    assertThat(extractor.extract(text))
            .containsExactly(PositiveNumber.from("1"),
                    PositiveNumber.from("2"), PositiveNumber.from("3"));
  }

  @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
  @ParameterizedTest
  @ValueSource(strings = {"//;\n1;2;3"})
  void customDelimiter(final String text) {
    assertThat(extractor.extract(text))
            .containsExactly(PositiveNumber.from("1"),
                    PositiveNumber.from("2"), PositiveNumber.from("3"));
  }

  @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 예외 처리를 한다.")
  @ParameterizedTest
  @NullAndEmptySource
  void emptyOrNull(final String text) {
    assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> extractor.extract(text));
  }

  @DisplayName(value = "음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
  @Test
  void negative() {
    assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> extractor.extract("-1"));
  }

  @DisplayName(value = "숫자가 아닌 경우 RuntimeException 예외 처리를 한다.")
  @Test
  void illegalArgs() {
    assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> extractor.extract("/"));
  }
}
