package kitchenpos.ordertable

import kitchenpos.domain.OrderTable

class OrderTableFixture {
    companion object {
        fun fixture(
            name: String = "테이블",
            numberOfGuests: Int = 0,
            occupied: Boolean = false
        ): OrderTable {
            val orderTable = OrderTable()
            orderTable.name = name
            orderTable.numberOfGuests = numberOfGuests
            orderTable.isOccupied = occupied
            return orderTable
        }
    }
}
