package kitchenpos.configuration;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.infra.KitchenridersClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("test")
public class KitchenridersClientConfiguration {

	@Bean(name = "fakeKitchenridersClient")
	@Primary
	public KitchenridersClient kitchenridersClient() {
		return new FakeKitchenridersClient();
	}

	public static class FakeKitchenridersClient extends KitchenridersClient {

		private int requestCount;

		@Override
		public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {
			requestCount++;
		}

		public int getRequestCount() {
			return requestCount;
		}
	}
}
