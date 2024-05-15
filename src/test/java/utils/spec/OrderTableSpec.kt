package utils.spec

import kitchenpos.domain.OrderTable
import java.util.UUID

object OrderTableSpec {
    fun of(occupied: Boolean = false, numberOfGuests: Int = 0): OrderTable {
        val orderTable = OrderTable()

        orderTable.id = UUID.randomUUID()
        orderTable.name = "1번"
        orderTable.isOccupied = occupied
        orderTable.numberOfGuests = numberOfGuests

        return orderTable
    }
}
