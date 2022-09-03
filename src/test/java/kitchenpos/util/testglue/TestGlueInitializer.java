package kitchenpos.util.testglue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

			validateParameterCount(method);

			TestGlueOperation testGlueOperation = method.getAnnotation(TestGlueOperation.class);
			String description = testGlueOperation.value();

			validateDescription(method, description);

			testGlueOperationContext.put(description, () -> {
				try {
					method.invoke(bean);
				} catch (IllegalAccessException | InvocationTargetException e) {
					throw new IllegalArgumentException(e);
				}
			});
		}
	}

	private void validateDescription(Method method, String description) {
		if (description.isBlank()) {
			throw new IllegalArgumentException(String.format("Method (%s) description cannot be empty", method.getName()));
		}
	}

	private void validateParameterCount(Method method) {
		int parameterCount = method.getParameterCount();
		if (parameterCount != 0) {
			throw new IllegalArgumentException(String.format("Method (%s) cannot have parameter.", method.getName()));
		}
	}
}
