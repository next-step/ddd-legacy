package calculator;

import java.util.Arrays;
import java.util.List;

public record Numbers(List<NonNegativeNumber> numbers) {

	public static Numbers from(final String[] inputCharacters) {
		return new Numbers(Arrays.stream(inputCharacters)
			.map(NonNegativeNumber::from)
			.toList());
	}

	public int sum() {
		return numbers.stream().mapToInt(NonNegativeNumber::value).sum();
	}
}
