package kitchenpos.util.testglue;

import java.util.ArrayList;
import java.util.List;

public class TestGlue {

	private final TestGlueOperationContext context;

	public TestGlue(TestGlueOperationContext context) {
		this.context = context;
	}

	public Builder builder() {
		return new Builder(context, new ArrayList<>());
	}

	public static class Builder {

		private final TestGlueOperationContext context;
		private final List<String> operations;

		public Builder(TestGlueOperationContext context, List<String> operations) {
			this.context = context;
			this.operations = operations;
		}

		public Builder given(String op) {
			this.operations.add(op);
			return this;
		}

		public Builder when(String op) {
			this.operations.add(op);
			return this;
		}

		public Builder then(String op) {
			this.operations.add(op);
			return this;
		}

		public void assertStart() {
			new TestGlueRunner(context, operations).assertStart();
		}
	}
}
