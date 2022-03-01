package calculator;

import java.util.ArrayList;
import java.util.List;

public class StringCalculator {

	private final List<NumberTokenizer> numberTokenizers;

	public StringCalculator(NumberTokenizer numberTokenizer) {
		this(List.of(numberTokenizer));
	}

	public StringCalculator(List<NumberTokenizer> numberTokenizers) {
		List<NumberTokenizer> l = new ArrayList<>(numberTokenizers);
		l.add(new DefaultTokenizer());
		this.numberTokenizers = l;
	}

	public int add(final String text) {
		if (text == null || text.isBlank()) {
			return 0;
		}

		final List<Integer> numbers = numberTokenizers.stream()
				.filter(tokenizer -> tokenizer.canTokenize(text))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("처리 가능한 토크나이저가 없습니다."))
				.tokenize(text);

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
