package calculator;

import java.util.List;
import java.util.Objects;

public class StringCalculator {

	private static final int DEFAULT_RESULT = 0;

	private final NumberTokenizers numberTokenizers;

	public StringCalculator(NumberTokenizers numberTokenizers) {
		Objects.requireNonNull(numberTokenizers);
		this.numberTokenizers = numberTokenizers;
	}

	public int add(final String text) {
		if (text == null || text.isBlank()) {
			return DEFAULT_RESULT;
		}

		List<Integer> numbers = numberTokenizers.tokenize(text);

		return add(numbers);
	}

	private int add(final List<Integer> numbers) {
		validateNegativeNumber(numbers);
		return numbers.stream().mapToInt(Integer::valueOf).sum();
	}

	private void validateNegativeNumber(final List<Integer> numbers) {
		numbers.forEach(number -> {
			if (number < 0) throw new IllegalArgumentException("음수를 더할 수 없습니다.");
		});
	}
}
