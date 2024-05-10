package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TextExtractorFactoryTest {

  @DisplayName(value = "지원하는 문자열 추출기가 존재하지 않는 경우 TextExtractionException 예외 처리를 한다.")
  @Test
  void getFail() {
    TextExtractorFactory textExtractorFactory = new TextExtractorFactory(
        new EmptyTextExtractor()
    );
    assertThatExceptionOfType(TextExtractionException.class)
        .isThrownBy(() -> textExtractorFactory.get("23"));
  }

  @DisplayName(value = "지원하는 문자열 추출기가 존재하지 않는 경우 조회할 수 있다.")
  @Test
  void get() {
    TextExtractorFactory textExtractorFactory = new TextExtractorFactory(
        new EmptyTextExtractor(),
        new CustomDelimiterTextExtractor(),
        new DefaultTextExtractor()
    );
    assertThat(textExtractorFactory.get("").getClass())
        .hasSameClassAs(EmptyTextExtractor.class);
    assertThat(textExtractorFactory.get("//;\n1;2;3").getClass())
        .hasSameClassAs(CustomDelimiterTextExtractor.class);
    assertThat(textExtractorFactory.get("2").getClass())
        .hasSameClassAs(DefaultTextExtractor.class);
  }

}
