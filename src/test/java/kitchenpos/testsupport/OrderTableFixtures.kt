package kitchenpos.testsupport

import java.util.UUID
import kitchenpos.domain.OrderTable

object OrderTableFixtures {
    fun createOrderTable(
        isOccupied: Boolean = false
    ): OrderTable {
        return OrderTable().apply {
            this.id = UUID.randomUUID()
            this.name = "test-order-table-name"
            this.numberOfGuests = 0
            this.isOccupied = isOccupied
        }
    }
}