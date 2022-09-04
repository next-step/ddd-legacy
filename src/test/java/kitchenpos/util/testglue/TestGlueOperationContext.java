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
		Operation operation = volume.get(find(description));

		return operation;
	}

	private String find(String description) {
		final String targetDescription = description.replaceAll("'[^']*'", "{}");

		return volume.keySet().stream()
			.filter(v -> v.equals(targetDescription))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException(String.format("can not found operation : %s", description)));
	}
}
