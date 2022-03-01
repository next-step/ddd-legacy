package calculator;

import java.util.List;

public class StringCalculator {

	public int add(final String text) {
		NumberTokenizer numberTokenizer = new SlashTokenizer();
		NumberTokenizer defaultTokenizer = new DefaultTokenizer();

		if (text == null || text.isBlank()) {
			return 0;
		}

		if (numberTokenizer.canTokenize(text)) {
			List<Integer> numbers = numberTokenizer.tokenize(text);
			return add(numbers);
		}

		List<Integer> numbers = defaultTokenizer.tokenize(text);
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
