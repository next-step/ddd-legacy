package kitchenpos.argument;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class NullAndNegativeBigDecimalArgumentsProvider implements ArgumentsProvider {
	private static final int NEGATIVE_VALUE = -10000;

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		return Stream.of(
			Arguments.of((BigDecimal)null),
			Arguments.of(new BigDecimal(NEGATIVE_VALUE))
		);
	}
}
