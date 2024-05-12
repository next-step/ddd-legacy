package calculator;

import java.util.Arrays;

public class StringCalculator {

	private static final String DEFAULT_DELIMITER = "[,:]";

	public int add(String text) {
		if (text == null || text.isEmpty()) {
			return 0;
		}

		String[] tokens = text.split(DEFAULT_DELIMITER);
		return Arrays.stream(tokens).mapToInt(Integer::parseInt).sum();
	}
}
