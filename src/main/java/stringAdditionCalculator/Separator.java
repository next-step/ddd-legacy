package stringAdditionCalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separator {
	public static final String BASE_SEPARATOR = "[,:]";
	public static final Pattern pattern = Pattern.compile("//(.*)\n(.*)");
	public static final int SEPARATOR_GROUP = 1;
	public static final int NUMBER_GROUP = 2;
	public static final int ALLOWED_LENGTH = 1;
	public static final int FIRST_INDEX = 0;

	public static String[] split(String input) {
		Matcher matcher = pattern.matcher(input);

		if (matcher.find()) {
			String customSeparator = matcher.group(SEPARATOR_GROUP);
			validate(customSeparator);
			return matcher.group(NUMBER_GROUP).split(customSeparator);
		}

		return input.split(BASE_SEPARATOR);
	}

	private static void validate(String customSeparator) {
		validateEmptiness(customSeparator);
		validateLength(customSeparator);
		validateType(customSeparator);
	}

	private static void validateType(String customSeparator) {
		if (Character.isDigit(customSeparator.charAt(FIRST_INDEX))) {
			throw new IllegalArgumentException("숫자 타입은 구분자로 사용할 수 없습니다");
		}
	}

	private static void validateLength(String customSeparator) {
		if (customSeparator.length() > ALLOWED_LENGTH) {
			throw new IllegalArgumentException("커스텀 구분자가 너무 깁니다");
		}
	}

	private static void validateEmptiness(String customSeparator) {
		if (customSeparator == null || customSeparator.isEmpty()) {
			throw new IllegalArgumentException("커스텀 구분자가 비어 있습니다");
		}
	}
}
