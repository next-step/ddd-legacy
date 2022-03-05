package stringAdditionCalculator;

public class StringAdditionCalculator {
	public int sum(String inputStringNumbers) {
		if (inputStringNumbers == null) {
			return Number.ZERO;
		}

		String[] stringNumbers = inputStringNumbers.split(",|:");

		Numbers numbers = new Numbers();
		for (String number : stringNumbers) {
			numbers.addNumber(new Number(number));
		}

		return numbers.getSum();
	}
}
