package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.mockk
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import kitchenpos.testsupport.FakeOrderTableRepository
import kitchenpos.testsupport.OrderTableFixtures

class OrderTableServiceChangeNumberOfGuestsTest : ShouldSpec({
    lateinit var savedOrderTable: OrderTable
    lateinit var orderTableRepository: OrderTableRepository
    lateinit var orderRepository: OrderRepository
    lateinit var service: OrderTableService

    beforeTest {
        orderTableRepository = FakeOrderTableRepository()
        orderRepository = mockk()
        service = OrderTableService(
            orderTableRepository,
            orderRepository
        )

        savedOrderTable = orderTableRepository.save(
            OrderTableFixtures.createOrderTable(
                isOccupied = true
            )
        )
    }

    context("주문 테이블 손님 수 변경") {
        should("성공") {
            // given
            val orderTableId = savedOrderTable.id
            val numberOfGuestsBeforeChange = savedOrderTable.numberOfGuests
            val request = createRequest(
                numberOfGuests = 2
            )

            // when
            val orderTableAfterChange = service.changeNumberOfGuests(
                orderTableId,
                request
            )

            // then
            numberOfGuestsBeforeChange shouldBe 0
            orderTableAfterChange.numberOfGuests shouldBe 2
        }

        should("실패 - 손님 수가 0 미만인 경우") {
            // given
            val orderTableId = savedOrderTable.id
            val request = createRequest(
                numberOfGuests = -1
            )

            // when
            val exception = shouldThrowAny {
                service.changeNumberOfGuests(
                    orderTableId,
                    request
                )
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 손님이 착석한 테이블이 아닌 경우") {
            // given
            savedOrderTable = orderTableRepository.save(
                OrderTableFixtures.createOrderTable(
                    isOccupied = false
                )
            )
            val orderTableId = savedOrderTable.id
            val request = createRequest(
                numberOfGuests = 2
            )

            // when
            val exception = shouldThrowAny {
                service.changeNumberOfGuests(
                    orderTableId,
                    request
                )
            }

            // then
            exception.shouldBeTypeOf<IllegalStateException>()
        }
    }
}) {
    companion object {
        private fun createRequest(
            numberOfGuests: Int
        ): OrderTable {
            return OrderTable().apply {
                this.numberOfGuests = numberOfGuests
            }
        }
    }
}
