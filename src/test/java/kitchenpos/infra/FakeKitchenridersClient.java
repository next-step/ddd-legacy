package kitchenpos.infra;

import kitchenpos.infra.Kitchenriders;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * <pre>
 * kitchenpos.application
 *      FakeKitchenridersClient
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-04-20 오후 11:12
 */

public class FakeKitchenridersClient implements Kitchenriders {

    @Override
    public void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress) {

    }
}
