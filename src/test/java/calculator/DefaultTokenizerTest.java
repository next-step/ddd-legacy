package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class DefaultTokenizerTest {

	private DefaultTokenizer defaultTokenizer;

	@BeforeEach
	void setUp() {
		defaultTokenizer = new DefaultTokenizer();
	}

	@DisplayName("콤마(,) 로 숫자를 쪼갤 수 있다.")
	@Test
	void comma() {
		List<PositiveOrZeroNumber> result = defaultTokenizer.tokenize("10,2");
		assertThat(result).containsExactly(new PositiveOrZeroNumber(10), new PositiveOrZeroNumber(2));
	}

	@DisplayName("콜론(:) 으로 숫자를 쪼갤 수 있다.")
	@Test
	void colon() {
		List<PositiveOrZeroNumber> result = defaultTokenizer.tokenize("10:2");
		assertThat(result).containsExactly(new PositiveOrZeroNumber(10), new PositiveOrZeroNumber(2));
	}

	@DisplayName("콤마(,) 와 콜론(:) 을 함께 사용하여 숫자를 쪼갤 수도 있다.")
	@Test
	void commaWithColon() {
		List<PositiveOrZeroNumber> result = defaultTokenizer.tokenize("10:2,5");
		assertThat(result).containsExactly(new PositiveOrZeroNumber(10), new PositiveOrZeroNumber(2), new PositiveOrZeroNumber(5));
	}

	@DisplayName("입력값에 숫자만 존재하는 경우 숫자 그대로 반환한다.")
	@ParameterizedTest
	@ValueSource(strings = {"0", "4"})
	void onlyNumber(String input) {
		PositiveOrZeroNumber expected = new PositiveOrZeroNumber(Integer.parseInt(input));
		List<PositiveOrZeroNumber> result = defaultTokenizer.tokenize(input);
		assertThat(result).containsExactly(expected);
	}

	@DisplayName("콤마(,) 와 콜론(:) 을 제외한 문자가 포함된 경우 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"145|1", "145!1", "@145"})
	void onlyNumbers(String input) {
		assertThatExceptionOfType(NumberFormatException.class)
				.isThrownBy(() -> defaultTokenizer.tokenize(input));
	}
}
