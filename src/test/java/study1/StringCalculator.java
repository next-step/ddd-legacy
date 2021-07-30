package study1;

import java.util.Arrays;
import org.springframework.util.StringUtils;

public class StringCalculator {

	private static final int DEFAULT_NUMBER = 0;

	public int add(final String text) {
		if (!StringUtils.hasText(text)) {
			return DEFAULT_NUMBER;
		}

		return summation(text).value();
	}

	private static PositiveNumber summation(final String text) {
		if (CustomSplitStrategy.applicable(text)) {
			return summation(text, new CustomSplitStrategy());
		}

		return summation(text, new DefaultSplitStrategy());
	}

	private static PositiveNumber summation(final String text, final SplitStrategy strategy) {
		return Arrays.stream(strategy.split(text))
			.map(PositiveNumber::valueOf)
			.reduce(PositiveNumber::add)
			.orElseThrow(RuntimeException::new);
	}
}
