package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

	private List<String> delimiterList = new ArrayList<>(List.of(",", ":"));

	public int add(String text) {
		if (text == null || text.isEmpty()) {
			return 0;
		}
		String target = text;
		Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
		if (m.find()) {
			String customDelimiter = m.group(1);
			delimiterList.add(customDelimiter);
			target = m.group(2);
		}
		String regex = String.join("|", delimiterList);
		return Arrays.stream(target.split(regex)).mapToInt(Integer::parseInt).sum();
	}
}
