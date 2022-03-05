package calculation.convert;

import calculation.number.Number;
import calculation.number.Numbers;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class StringSeparator {

	private Numbers deriveResults(String formula) {
		if (Objects.isNull(formula) || formula.trim().isEmpty()) {
			return Numbers.EMPTY;
		}
		String[] strings = Delimiter.separateUsingDelimiter(formula);
		return new Numbers(Arrays.stream(strings).map(Number::convertStringToNumber).collect(Collectors.toList()));
	}

	public int getResult(String formula) {
		return deriveResults(formula).sum();
	}

}
