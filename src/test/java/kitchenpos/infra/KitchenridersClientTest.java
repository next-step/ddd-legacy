package kitchenpos.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;

@ExtendWith(MockitoExtension.class)
class KitchenridersClientTest {

    @Test
    @DisplayName("아무 동작하지 않음")
    void name() {
        KitchenridersClient kitchenridersClient = new KitchenridersClient();
        assertThatNoException().isThrownBy(() ->
                        kitchenridersClient.requestDelivery(UUID.randomUUID(), new BigDecimal(1), "배달 주소"));
    }
}