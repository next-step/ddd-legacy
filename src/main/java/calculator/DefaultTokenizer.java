package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultTokenizer implements NumberTokenizer {

	private static final String DEFAULT_REGEX = "[,:]";

	@Override
	public boolean canTokenize(final String text) {
		return true;
	}

	@Override
	public List<Integer> tokenize(final String text) {
		final String[] tokens = text.split(DEFAULT_REGEX);
		return Arrays.stream(tokens)
				.mapToInt(Integer::valueOf)
				.boxed()
				.collect(Collectors.toList());
	}
}
