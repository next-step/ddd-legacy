package stringcalculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class OperandParser {
	private static final String DEFAULT_INPUT_REGEX = "//(.)\n(.*)";
	private static final String DEFAULT_OPERAND_DELIMITER_REGEX = ",|:";

	public List<Operand> parse(String input) {
		if (input == null || input.isBlank()) {
			return List.of(new Operand("0"));
		}

		return Stream.of(extractTokens(input))
			.map(Operand::new)
			.toList();
	}

	private static String[] extractTokens(final String input) {
		Matcher m = Pattern.compile(DEFAULT_INPUT_REGEX).matcher(input);
		String[] tokens;

		if (m.find()) {
			String customDelimiter = m.group(1);
			tokens = m.group(2).split(Pattern.quote(customDelimiter));
		} else {
			tokens = input.split(DEFAULT_OPERAND_DELIMITER_REGEX);
		}

		return tokens;
	}
}
