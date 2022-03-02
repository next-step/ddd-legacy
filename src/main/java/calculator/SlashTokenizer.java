package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class SlashTokenizer implements NumberTokenizer {

	private static final Pattern SLASH_PATTERN = Pattern.compile("//(.)\n(.*)");

	@Override
	public boolean canTokenize(final String text) {
		return SLASH_PATTERN.matcher(text).find();
	}

	@Override
	public List<PositiveOrZeroNumber> tokenize(final String text) {
		final Matcher matcher = SLASH_PATTERN.matcher(text);

		if (!matcher.find()) {
			throw new IllegalArgumentException("유효하지 않은 커스텀 텍스트");
		}

		final String tokenizer = matcher.group(1);
		final String tokenizableText = matcher.group(2);

		return Arrays.stream(tokenizableText.split(tokenizer))
				.mapToInt(Integer::valueOf)
				.mapToObj(PositiveOrZeroNumber::new)
				.collect(Collectors.toList());
	}
}
