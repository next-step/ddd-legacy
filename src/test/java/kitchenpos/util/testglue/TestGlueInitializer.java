package kitchenpos.util.testglue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;

public class TestGlueInitializer implements ApplicationContextAware {

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		var context = (GenericApplicationContext) applicationContext;

		context.registerBean(TestGlueOperationContext.class);
		context.registerBean(TestGlue.class);

		awareInit(context);
		operationInit(context);
	}

	private void awareInit(GenericApplicationContext context) {
		Map<String, TestGlueContextAware> testGlueContextAwares = context.getBeansOfType(TestGlueContextAware.class);
		TestGlueValueContext testGlueValueContext = new TestGlueValueContext();

		testGlueContextAwares.values().forEach(v -> v.testGlueContext(testGlueValueContext));
	}

	private void operationInit(GenericApplicationContext context) {
		var testGlueOperationContext = context.getBean(TestGlueOperationContext.class);

		var testGlueConfigurationNames = context.getBeanNamesForAnnotation(TestGlueConfiguration.class);

		for (String testGlueConfigurationName : testGlueConfigurationNames) {
			Object bean = context.getBean(testGlueConfigurationName);
			setTestGlueOperation(bean, testGlueOperationContext);
		}
	}

	private void setTestGlueOperation(Object bean, TestGlueOperationContext testGlueOperationContext) {
		Method[] methods = bean.getClass().getMethods();
		for (Method method : methods) {
			if (!method.isAnnotationPresent(TestGlueOperation.class)) {
				continue;
			}

			TestGlueOperation testGlueOperation = method.getAnnotation(TestGlueOperation.class);
			String description = testGlueOperation.value();

			validateDescription(method, description);

			testGlueOperationContext.put(description, parameters -> {
				try {
					method.invoke(bean, parameters);
				} catch (Exception e) {
					Throwable cause = e.getCause();
					if (cause instanceof AssertionError) {
						throw (AssertionError) cause;
					}

					throw new RuntimeException(String.format(
						"[메서드 실행 실패]\nmethod name : %s\ndescription : %s\nparameters : %s",
						method.getName(),
						description,
						Arrays.toString(parameters)
					), e);
				}
			});
		}
	}

	private void validateDescription(Method method, String description) {
		if (description.isBlank()) {
			throw new IllegalArgumentException(String.format("Method (%s) description cannot be empty", method.getName()));
		}
	}
}
