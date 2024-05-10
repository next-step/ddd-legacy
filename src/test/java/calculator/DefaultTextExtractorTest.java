package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultTextExtractorTest {
  private DefaultTextExtractor defaultTextExtractor;

  @BeforeEach
  void setUp() {
    defaultTextExtractor = new DefaultTextExtractor();
  }

  @DisplayName(value = "모든 문자열의 분리를 지원한다.")
  @ParameterizedTest
  @ValueSource(strings = {"", "as", "23,45"})
  void isSupport(final String text) {
    assertThat(defaultTextExtractor.isSupport(text)).isTrue();
  }

  @DisplayName(value = "모든 문자열을 기본 구분자를 기준으로 추출할 수 있다.")
  @Test
  void extract() {
    String text1 = "23 , 45";
    String text2 = "23";
    String text3 = "23:45";

    assertThat(defaultTextExtractor.extract(text1))
        .isEqualTo(new String[]{"23 ", " 45"});
    assertThat(defaultTextExtractor.extract(text2))
        .isEqualTo(new String[]{"23"});
    assertThat(defaultTextExtractor.extract(text3))
        .isEqualTo(new String[]{"23", "45"});
  }
}
