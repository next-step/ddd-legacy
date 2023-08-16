package calculator;

import java.util.List;
import java.util.stream.Stream;

public class StringCalculator {
	private static final int ZERO_OF_STRING_CALCULATOR = 0;
	private static final List<String> ADD_DELIMITER = List.of(",", ":");

	public int add(String text) {
		if (isNullValue(text)) {
			return ZERO_OF_STRING_CALCULATOR;
		}
		if (isSingleText(text)) {
			getNumeric(text);
		}

		return Stream.of(splittingText(text))
			.mapToInt(this::getNumeric)
			.sum();
	}

	private boolean isNullValue(String text) {
		return text == null || text.isEmpty();
	}

	private boolean isSingleText(String text) {
		return text.length() == 1;
	}

	private int getNumeric(String text) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			throw new RuntimeException("숫자가 아닌 값이 존재 합니다.");
		}
	}

	private String[] splittingText(String text) {
		String regex = String.format("[%s]"
			, String.join("", ADD_DELIMITER));
		return text.split(regex);
	}
}
