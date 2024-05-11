package domain

import kitchenpos.domain.Product
import java.math.BigDecimal
import java.util.UUID

object ProductFixtures {
    fun makeProductOne(): Product {
        return Product().apply {
            this.id = UUID.randomUUID()
            this.name = "후라이드 치킨"
            this.price = BigDecimal.valueOf(16000)
        }
    }
}
