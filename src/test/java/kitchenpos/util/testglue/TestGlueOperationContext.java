package kitchenpos.util.testglue;

import java.util.HashMap;
import java.util.Map;

public class TestGlueOperationContext {

	private final Map<String, Operation> volume;

	public TestGlueOperationContext() {
		this(new HashMap<>());
	}

	public TestGlueOperationContext(Map<String, Operation> volume) {
		this.volume = volume;
	}

	public void put(String description, Operation operation) {
		volume.put(description, operation);
	}

	public Operation get(String description) {
		Operation operation = volume.get(description);

		if (operation == null) {
			throw new IllegalArgumentException("can not found operation");
		}

		return operation;
	}
}
