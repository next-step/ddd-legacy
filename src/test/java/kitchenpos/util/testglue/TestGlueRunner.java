package kitchenpos.util.testglue;

import java.util.List;

public class TestGlueRunner {

	private final TestGlueOperationContext testGlueOperationContext;
	private final List<String> operations;

	public TestGlueRunner(TestGlueOperationContext testGlueOperationContext, List<String> operations) {
		this.testGlueOperationContext = testGlueOperationContext;
		this.operations = operations;
	}

	public void assertStart() {
		operations.stream()
			.map(testGlueOperationContext::get)
			.forEach(Operation::run);
	}
}
