package domain

import kitchenpos.domain.OrderTable
import java.util.*

object OrderTableFixtures {
    fun makeOrderTableOne(): OrderTable {
        return OrderTable().apply {
            this.id = UUID.fromString("6924640b-b0fc-4c86-84f9-b750eeba0205")
            this.name = "테이블 1"
            this.numberOfGuests = 4
            this.isOccupied = true
        }
    }

    // 초기 order table
    fun makeOrderTableTwo(): OrderTable {
        return OrderTable().apply {
            this.id = UUID.fromString("6924640b-b0fc-4c86-84f9-b750eeba0205")
            this.name = "테이블 1"
            this.numberOfGuests = 0
            this.isOccupied = false
        }
    }
}
