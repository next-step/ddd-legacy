package kitchenpos.util;

import java.util.ArrayList;
import java.util.List;

public class TestGlue {

	private final List<Operation> operations;

	public TestGlue() {
		this(new ArrayList<>());
	}

	public TestGlue(List<Operation> operations) {
		this.operations = operations;
	}

	public void run() {
		this.operations.forEach(Operation::run);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private List<Operation> operations;

		public Builder then(Operation op) {
			this.operations.add(op);
			return this;
		}

		public TestGlue build() {
			return new TestGlue(operations);
		}
	}
}
