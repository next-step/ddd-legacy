package calculator;

import java.util.Arrays;

public class StringCalculator {
	public int add(String text) {
		if (text == null || text.isEmpty()) {
			return 0;
		}
		return Arrays.stream(Splitter.splitText(text))
			.map(PositiveNumber::new)
			.mapToInt(PositiveNumber::getNumber)
			.sum();
	}
}
