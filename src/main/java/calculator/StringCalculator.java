package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator {

	private static final String DEFAULT_REGEX = "[,:]";

	public int add(final String text) {
		NumberTokenizer numberTokenizer = new SlashTokenizer();
		if (text == null || text.isBlank()) {
			return 0;
		}

		if (numberTokenizer.canTokenize(text)) {
			List<Integer> numbers = numberTokenizer.tokenize(text);
			return add(numbers);
		}

		List<Integer> numbers = tokenizeDefault(text);
		return add(numbers);
	}

	private List<Integer> tokenizeDefault(final String text) {
		final String[] tokens = text.split(DEFAULT_REGEX);
		return Arrays.stream(tokens)
				.mapToInt(Integer::valueOf)
				.boxed()
				.collect(Collectors.toList());
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
