package calculator;

public class StringCalculator {

	public int calculate(final String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}

		return Integer.parseInt(input);
	}
}
