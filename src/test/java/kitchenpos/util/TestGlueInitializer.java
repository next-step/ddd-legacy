package kitchenpos.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;

public class TestGlueInitializer implements ApplicationContextAware {

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		var context = (AnnotationConfigApplicationContext) applicationContext;
		context.registerBean(TestGlueOperationContext.class);

		var testGlueOperationContext = context.getBean(TestGlueOperationContext.class);

		var testGlueConfigurationNames = context.getBeanNamesForAnnotation(TestGlueConfiguration.class);

		for (String testGlueConfigurationName : testGlueConfigurationNames) {
			Object bean = context.getBean(testGlueConfigurationName);
			setTestGlueOperation(bean, testGlueOperationContext);
		}
	}

	@GetMapping("test")
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
