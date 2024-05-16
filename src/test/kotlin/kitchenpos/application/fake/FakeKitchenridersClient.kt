package kitchenpos.application.fake

import kitchenpos.domain.KitchenridersClient
import java.math.BigDecimal
import java.util.*

class FakeKitchenridersClient : KitchenridersClient {
    override fun requestDelivery(
        orderId: UUID,
        amount: BigDecimal,
        deliveryAddress: String,
    ) {
    }
}
