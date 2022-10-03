package kitchenpos.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class TestIsolationSupport {

	@Autowired
	private DataInitializer dataInitializer;

	@Autowired
	private TestGlueContextAwareHelper testGlueContextAwareHelper;

	@BeforeEach
	void setUp() {
		dataInitializer.execute();
		testGlueContextAwareHelper.clear();
	}
}
