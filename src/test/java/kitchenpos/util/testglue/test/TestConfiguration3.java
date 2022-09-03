package kitchenpos.util.testglue.test;

import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueOperation;

@TestGlueConfiguration
public class TestConfiguration3 {

	private int a = 0;

	@TestGlueOperation("test3")
	public void test() {
		a++;
	}

	public int getA() {
		return a;
	}
}
