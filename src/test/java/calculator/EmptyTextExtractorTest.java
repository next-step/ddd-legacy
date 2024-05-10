package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EmptyTextExtractorTest {
  private EmptyTextExtractor emptyTextExtractor;

  @BeforeEach
  void setUp() {
    emptyTextExtractor = new EmptyTextExtractor();
  }

  @DisplayName(value = "빈 문자열 분리를 지원한다.")
  @ParameterizedTest
  @ValueSource(strings = {"", "      "})
  void isSupport(final String text) {
    assertThat(emptyTextExtractor.isSupport(text)).isTrue();
  }

  @DisplayName(value = "빈 문자열 분리를 지원하지 않는다.")
  @ParameterizedTest
  @ValueSource(strings = {"/\n1;2;3", "12,34,56"})
  void isNotSupport(final String text) {
    assertThat(emptyTextExtractor.isSupport(text)).isFalse();
  }

  @DisplayName(value = "빈 문자열은 0으로 추출할 수 있다.")
  @ParameterizedTest
  @ValueSource(strings = {"", "      "})
  void extract(final String text) {
    assertThat(emptyTextExtractor.extract(text))
        .isEqualTo(new String[]{"0"});
  }

  @DisplayName(value = "빈 문자열이 아닌 경우 TextExtractionException 예외 처리를 한다.")
  @ParameterizedTest
  @ValueSource(strings = {"/\n1;2;3", "12,34,56"})
  void extractFail(final String text) {
    assertThatExceptionOfType(TextExtractionException.class)
        .isThrownBy(() -> emptyTextExtractor.extract(text));
  }
}
