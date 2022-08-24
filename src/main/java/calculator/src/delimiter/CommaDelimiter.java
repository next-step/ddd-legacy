package calculator.src.delimiter;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public class CommaDelimiter implements Delimiter{

	private static final String DELIMITER = ",";

	@Override
	public List<String> tokenize(String input) {
		return Arrays.stream(input.split(DELIMITER))
			.map(String::trim)
			.collect(toList());
	}
}
