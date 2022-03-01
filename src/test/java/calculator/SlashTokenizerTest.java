package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SlashTokenizerTest {

	private SlashTokenizer slashTokenizer;

	@BeforeEach
	void setUp() {
		slashTokenizer = new SlashTokenizer();
	}

	@DisplayName("//와 줄바꿈(\\n) 사이에 토크나이저를 입력하면 토크나이징 할 수 있다.")
	@ParameterizedTest
	@ValueSource(strings = {"//`\n1`3", "//#\n1#33#55"})
	void canTokenize(String input) {
		boolean result = slashTokenizer.canTokenize(input);
		assertThat(result).isTrue();
	}

	@DisplayName("//가 없거나 줄바꿈(\\n) 이 없다면 토크나이징 할 수 없다.")
	@ParameterizedTest
	@ValueSource(strings = {"/`\n1`3", "//`1`3"})
	void canNotTokenize(String input) {
		boolean result = slashTokenizer.canTokenize(input);
		assertThat(result).isFalse();
	}

	@DisplayName("토크나이징 가능한 문자열은 토크나이징이 가능하다.")
	@Test
	void tokenizeSuccess() {
		List<Integer> result = slashTokenizer.tokenize("//%\n1%4%-6");
		assertThat(result).containsExactly(1, 4, -6);
	}

	@DisplayName("토크나이징 할 수 없는 문자열로 토크나이징 하는 경우 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"/`\n1`3", "//`1`3"})
	void tokenizeError(String input) {
		assertThatThrownBy(() -> slashTokenizer.tokenize(input))
				.isInstanceOf(IllegalArgumentException.class);
	}
}
