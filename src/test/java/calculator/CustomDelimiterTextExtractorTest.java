package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CustomDelimiterTextExtractorTest {
  private CustomDelimiterTextExtractor customDelimiterTextExtractor;

  @BeforeEach
  void setUp() {
    customDelimiterTextExtractor = new CustomDelimiterTextExtractor();
  }

  @DisplayName(value = "커스텀 구분자로 문자열 분리를 지원한다.")
  @ParameterizedTest
  @ValueSource(strings = {"//;\n1;2;3", "//,\n12,34,56"})
  void isSupport(final String text) {
    assertThat(customDelimiterTextExtractor.isSupport(text)).isTrue();
  }

  @DisplayName(value = "커스텀 구분자로 문자열 분리를 지원하지 않는다.")
  @ParameterizedTest
  @ValueSource(strings = {"/\n1;2;3", "12,34,56"})
  void isNotSupport(final String text) {
    assertThat(customDelimiterTextExtractor.isSupport(text)).isFalse();
  }

  @DisplayName(value = "커스텀 구분자로 문자열 분리를 할 수 있다.")
  @Test
  void extract() {
    String text = "//,\n12,34,56";
    assertThat(customDelimiterTextExtractor.extract(text))
        .isEqualTo(new String[]{"12", "34", "56"});
  }

  @DisplayName(value = "커스텀 구분자로 문자열 분리할 수 없을 경우 TextExtractionException 예외 처리를 한다.")
  @ParameterizedTest
  @ValueSource(strings = {"/\n1;2;3", "12,34,56"})
  void extractFail(final String text) {
    assertThatExceptionOfType(TextExtractionException.class)
        .isThrownBy(() -> customDelimiterTextExtractor.extract(text));
  }
}
