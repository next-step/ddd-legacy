package random

import kitchenpos.domain.OrderTable
import java.util.UUID
import kotlin.random.Random

fun randomOrderTable(
    id: UUID = randomOrderTableId(),
    name: String? = randomOrderTableName(),
    numberOfGuests: Int = Random.nextInt(0, Int.MAX_VALUE),
    occupied: Boolean = Random.nextBoolean(),
): OrderTable {
    return OrderTable().apply {
        this.id = id
        this.name = name
        this.numberOfGuests = numberOfGuests
        this.isOccupied = occupied
    }
}

fun randomOrderTableId() = UUID.randomUUID()

fun randomOrderTableName() = listOf("1번테이블", "2번테이블", "3번테이블", "4번테이블").random()
