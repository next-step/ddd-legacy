package calculator;

import java.util.List;
import java.util.Objects;

public class StringCalculator {

	private static final int DEFAULT_RESULT = 0;

	private final NumberTokenizers numberTokenizers;

	public StringCalculator(final NumberTokenizers numberTokenizers) {
		Objects.requireNonNull(numberTokenizers);
		this.numberTokenizers = numberTokenizers;
	}

	public int add(final String text) {
		if (text == null || text.trim().isEmpty()) {
			return DEFAULT_RESULT;
		}

		if (isPositiveOrZero(text)) {
			return Integer.parseInt(text);
		}

		final List<Integer> numbers = numberTokenizers.tokenize(text);

		final PositiveOrZeroNumber result = numbers.stream()
				.map(PositiveOrZeroNumber::new)
				.reduce(PositiveOrZeroNumber::plus)
				.orElse(new PositiveOrZeroNumber(DEFAULT_RESULT));

		return result.getValue();
	}

	private boolean isPositiveOrZero(String text) {
		try {
			return Integer.parseInt(text) >= 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
