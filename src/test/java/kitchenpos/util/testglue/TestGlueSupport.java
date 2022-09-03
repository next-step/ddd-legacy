package kitchenpos.util.testglue;

public class TestGlueSupport implements TestGlueContextAware {

	private TestGlueValueContext testGlueValueContext;

	public void put(String key, Object value) {
		testGlueValueContext.put(key, value);
	}

	public <T> T getAsType(String key, Class<T> type) {
		return testGlueValueContext.getAsType(key, type);
	}

	@Override
	public void testGlueContext(TestGlueValueContext testGlueValueContext) {
		this.testGlueValueContext = testGlueValueContext;
	}
}
