package calculator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SplitterTest {

	@DisplayName("쉼표(,) 또는 콜론(:)을 기준으로 문자열을 분리한다.")
	@ParameterizedTest
	@ValueSource(strings = {"1,2:3"})
	void splitText(final String text) {
		assertThat(Splitter.splitText(text)).containsExactly("1", "2", "3");
	}

	@DisplayName("커스텀 구분자를 기준으로 문자열을 분리한다.")
	@ParameterizedTest
	@ValueSource(strings = {"//;\n1;2,3"})
	void splitTextWithCustomDelimiter(final String text) {
		assertThat(Splitter.splitText(text)).containsExactly("1", "2", "3");
	}
}
