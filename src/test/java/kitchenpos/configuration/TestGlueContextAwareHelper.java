package kitchenpos.configuration;

import kitchenpos.util.testglue.TestGlueContextAware;
import kitchenpos.util.testglue.TestGlueValueContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@Component
@ActiveProfiles("test")
public class TestGlueContextAwareHelper implements TestGlueContextAware {

	private TestGlueValueContext testGlueValueContext;

	@Override
	public void testGlueContext(TestGlueValueContext testGlueValueContext) {
		this.testGlueValueContext = testGlueValueContext;
	}

	public void clear() {
		testGlueValueContext.clear();
	}
}
