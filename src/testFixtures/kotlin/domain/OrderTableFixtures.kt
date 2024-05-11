package domain

import kitchenpos.domain.OrderTable
import java.util.*

object OrderTableFixtures {
    fun makeOrderTableOne(): OrderTable {
        return OrderTable().apply {
            this.id = UUID.randomUUID()
            this.name = "테이블 1"
            this.numberOfGuests = 4
            this.isOccupied = false
        }
    }
}
