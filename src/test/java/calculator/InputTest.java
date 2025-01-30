package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

class InputTest {

	@DisplayName("문자열을 구분자로 분리해 숫자의 합을 가져올 수 있다.")
	@CsvSource({"'1,1:2'", "'//;\n1;1;2'", "'//!\n1!1!2'", "'//@\n1@1@2'"})
	@ParameterizedTest
	void getPositiveNumbers(String inputValue) {
		// given
		final Input input = new Input(inputValue);

		// when
		int sum = input.getSum();

		// then
		assertThat(sum).isEqualTo(4);
	}

	@DisplayName("숫자가 아니거나, 음수인 문자열을 입력하면 예외를 발생시킨다.")
	@CsvSource({"삼", "-1", "abc", "!2"})
	@ParameterizedTest
	void getPositiveNumbersException(String inputString) {
		// when, then
		assertThatThrownBy(
			() -> {
				Input input = new Input(inputString);
			})
			.isInstanceOf(RuntimeException.class)
			.satisfies(
				e ->
					assertThat(e.getMessage())
						.matches(
							msg ->
								msg.equals(ErrorMessage.NOT_NUMBER)
									|| msg.equals(ErrorMessage.NEGATIVE_NUMBER)));
	}

	@DisplayName("문자열을 ',', ':' 구분자로 split할 수 있다.")
	@Test
	void split() {
		// given
		String inputValue = "1,1:2";
		final Input input = new Input(inputValue);

		// when
		String[] inputValues = ReflectionTestUtils.invokeMethod(input, "split", inputValue);

		// then
		assertThat(inputValues.length).isEqualTo(3);
		assertThat(inputValues[0]).isEqualTo("1");
		assertThat(inputValues[1]).isEqualTo("1");
		assertThat(inputValues[2]).isEqualTo("2");
	}

	@DisplayName("문자열을 커스텀 구분자로 split할 수 있다.")
	@CsvSource({"'//;\n1;2;3'", "'//!\n1!2!3'", "'//@\n1@2@3'"})
	@ParameterizedTest
	void splitCustom(String inputValue) {
		// given
		final Input input = new Input(inputValue);

		// when
		String[] inputValues = ReflectionTestUtils.invokeMethod(input, "split", inputValue);

		// then
		assertThat(inputValues.length).isEqualTo(3);
		assertThat(inputValues[0]).isEqualTo("1");
		assertThat(inputValues[1]).isEqualTo("2");
		assertThat(inputValues[2]).isEqualTo("3");
	}
}
