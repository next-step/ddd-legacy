package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DummyKitchenridersClientTest {

    private final Random random = new Random();

    // SUT

    private final DummyKitchenridersClient dummyKitchenridersClient = new DummyKitchenridersClient();

    @DisplayName("배달 요청시 아무것도 반환하지 않아야 한다.")
    @Test
    void ppeasbuu() {
        // when
        final UUID orderId = UUID.randomUUID();
        final long amount = this.random.nextLong();
        final String deliveryAddress = "FooBar";

        // then
        Assertions.assertThatNoException()
            .isThrownBy(() -> this.dummyKitchenridersClient.requestDelivery(
                orderId,
                BigDecimal.valueOf(amount),
                deliveryAddress
            ));
    }
}
