package calculator.src;

import static java.util.stream.Collectors.toUnmodifiableList;

import calculator.src.delimiter.Delimiter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorString {

	private static final Pattern pattern = Pattern.compile("//(.*)\\n(.*)");

	private final Delimiter delimiter;
	private final String value;

	public CalculatorString(String value) {
		Matcher matcher = pattern.matcher(value);

		if (matcher.find()) {
			this.delimiter = createCustomDelimiter(matcher);
			this.value = matcher.group(2);
			return;
		}

		this.delimiter = null;
		this.value = value;
	}

	private Delimiter createCustomDelimiter(Matcher matcher) {
		String customDelimiter = matcher.group(1);

		return new Delimiter(customDelimiter) {
		};
	}

	public List<CalculatorString> tokenizingBy(Delimiter delimiter) {
		return Arrays.stream(value.split(delimiter.getValue()))
			.map(String::trim)
			.map(CalculatorString::new)
			.collect(toUnmodifiableList());
	}

	public Optional<Delimiter> extractCustomDelimiter() {
		return Optional.ofNullable(delimiter);
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CalculatorString that = (CalculatorString) o;
		return Objects.equals(delimiter, that.delimiter) && Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(delimiter, value);
	}
}
