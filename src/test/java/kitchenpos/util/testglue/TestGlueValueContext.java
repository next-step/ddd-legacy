package kitchenpos.util.testglue;

import java.util.HashMap;
import java.util.Map;

public class TestGlueValueContext {

	private final Map<String, Object> storage;

	public TestGlueValueContext() {
		this.storage = new HashMap<>();
	}

	public void put(String key, Object value) {
		storage.put(key, value);
	}

	public <T> T getAsType(String key, Class<T> type) {
		Object o = storage.get(key);
		if (o == null) {
			return null;
		}

		return type.cast(o);
	}
}
