package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {

	private static final Pattern SLASH_PATTERN = Pattern.compile("//(.)\n(.*)");
	private static final String DEFAULT_REGEX = "[,:]";

	public int add(final String text) {
		if (text == null || text.isBlank()) {
			return 0;
		}

		if (SLASH_PATTERN.matcher(text).find()) {
			List<Integer> numbers = tokenizeSlash(text);
			return add(numbers);
		}

		List<Integer> numbers = tokenizeDefault(text);
		return add(numbers);
	}

	private List<Integer> tokenizeSlash(final String text) {
		final Matcher matcher = SLASH_PATTERN.matcher(text);

		if (!matcher.find()) {
			throw new IllegalArgumentException("유효하지 않은 커스텀 텍스트");
		}

		final String tokenizer = matcher.group(1);
		final String textx = matcher.group(2);
		return Arrays.stream(textx.split(tokenizer)).mapToInt(Integer::valueOf).boxed().collect(Collectors.toList());
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
