package calculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.util.Strings;

public record Splitter(List<String> delimiters) {

	public static Splitter from(final String... delimiters) {
		return new Splitter(List.of(delimiters));
	}

	public Splitter addDelimiter(final String delimiter) {
		final List<String> result = new ArrayList<>(delimiters);
		result.add(delimiter);

		return new Splitter(Collections.unmodifiableList(result));
	}

	public String[] split(final String input) {
		return input.split(getDelimiterPattern());
	}

	private String getDelimiterPattern() {
		return Strings.join(delimiters, '|');
	}
}
