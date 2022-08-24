package calculator;

import java.util.Arrays;
import java.util.regex.Pattern;

class StringCalculationSpec {

	private static final Pattern NUMBER_REGEXP = Pattern.compile("^\\d+$");
	private static final String INVALID_INPUT_TEMPLATE = "음수 혹은 문자는 입력할 수 없습니다. 현재 값: ";

	private StringCalculationSpec() {
	}

	static StringCalculationSpec getInstance() {
		return new StringCalculationSpec();
	}

	boolean isNullOrEmpty(String input) {
		return input == null || input.isEmpty();
	}

	boolean isSingleNumber(String input) {
		return input.length() == 1;
	}

	void checkNumbers(String[] tokens) {
		Arrays.stream(tokens)
			.filter(this::isNotPositiveNumber)
			.findAny()
			.ifPresent(number -> {
				throw new IllegalArgumentException(INVALID_INPUT_TEMPLATE + number);
			});
	}

	private boolean isNotPositiveNumber(String input) {
		return !NUMBER_REGEXP.matcher(input).matches();
	}
}
