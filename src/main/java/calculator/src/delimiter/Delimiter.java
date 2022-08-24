package calculator.src.delimiter;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

abstract class Delimiter {

	public List<String> tokenize(String input) {
		return Arrays.stream(input.split(delimiter()))
			.map(String::trim)
			.collect(toList());
	}

	abstract protected String delimiter();
}
