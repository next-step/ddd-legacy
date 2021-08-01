package stringcalculator;

public class StringCalculator {

	public int add(String inputNumber) {

		if (inputNumber == null || inputNumber.trim().isEmpty()) {
			return 0;
		}

		return Integer.parseInt(inputNumber);
	}
}
