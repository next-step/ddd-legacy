package kitchenpos.infra;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * <pre>
 * kitchenpos.infra
 *      Kitchenriders
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-04-20 오후 11:11
 */


public interface Kitchenriders {
    void requestDelivery(UUID orderId, BigDecimal amount, String deliveryAddress);
}
