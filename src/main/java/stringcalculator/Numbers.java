package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Numbers {
	private final List<Number> numbers = new ArrayList<>();

	private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

	private final int LENGTH_OF_NUMBER_FOR_SUM = 2;

	public Numbers(String inputNumbers) {
		if (inputNumbers == null || inputNumbers.trim().isEmpty()) {
			numbers.add(new Number("0"));
			return;
		}

		Matcher m = pattern.matcher(inputNumbers);

		if (m.find()) {
			String customDelimiter = m.group(1);
			String[] tokens= m.group(2).split(customDelimiter);
			numbers.addAll(getNumbers(tokens));
			return;
		}

		String[] tokens = inputNumbers.split(",|:");
		numbers.addAll(getNumbers(tokens));
	}

	public boolean isNumberZero() {
		return (numbers.size()==0 && numbers.contains(new Number("0")));
	}

	private List<Number> getNumbers(String[] tokens) {
		return Arrays.stream(tokens).map(Number::new).collect(Collectors.toList());
	}

	public boolean isSummable() {
		return (numbers.size() >= LENGTH_OF_NUMBER_FOR_SUM);
	}

	public int sum() {
		return numbers.stream().mapToInt(number -> number.toInteger()).sum();
	}

	public int firstNumber() {
		return numbers.get(0).toInteger();
	}
}
