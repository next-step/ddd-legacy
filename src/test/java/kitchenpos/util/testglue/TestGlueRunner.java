package kitchenpos.util.testglue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestGlueRunner {

	private static final Pattern tokenPattern = Pattern.compile("('[^']*')");

	private final TestGlueOperationContext testGlueOperationContext;
	private final List<String> operations;

	public TestGlueRunner(TestGlueOperationContext testGlueOperationContext, List<String> operations) {
		this.testGlueOperationContext = testGlueOperationContext;
		this.operations = operations;
	}

	public void assertStart() {
		for (String operation : operations) {
			Object[] parameters = extractParameter(operation);

			testGlueOperationContext.get(operation).run(parameters);
		}
	}

	private Object[] extractParameter(String description) {
		Matcher matcher = tokenPattern.matcher(description);

		List<String> result = new ArrayList<>();
		while (matcher.find()) {
			result.add(matcher.group().replaceAll("'", "").trim());

			description = description.substring(matcher.end());
			matcher = tokenPattern.matcher(description);
		}

		return result.toArray(String[]::new);
	}
}
