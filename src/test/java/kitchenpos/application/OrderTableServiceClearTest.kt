package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import kitchenpos.testsupport.FakeOrderTableRepository
import kitchenpos.testsupport.OrderTableFixtures

class OrderTableServiceClearTest : ShouldSpec({
    lateinit var savedOrderTable: OrderTable
    lateinit var orderTableRepository: OrderTableRepository
    lateinit var orderRepository: OrderRepository
    lateinit var service: OrderTableService

    beforeTest {
        orderTableRepository = FakeOrderTableRepository()
        orderRepository = mockk {
            every {
                existsByOrderTableAndStatusNot(any(), any())
            } returns false
        }
        service = OrderTableService(
            orderTableRepository,
            orderRepository
        )

        savedOrderTable = orderTableRepository.save(
            OrderTableFixtures.createOrderTable()
        )
    }

    context("주문 테이블 공석 처리") {
        should("성공") {
            // given
            val orderTableId = savedOrderTable.id
            savedOrderTable.apply {
                isOccupied = true
                numberOfGuests = 2
            }
            val isOccupiedBeforeClear = savedOrderTable.isOccupied
            val numberOfGuestsBeforeClear = savedOrderTable.numberOfGuests

            // when
            val orderTableAfterSit = service.clear(orderTableId)

            // then
            isOccupiedBeforeClear shouldBe true
            numberOfGuestsBeforeClear shouldBe 2
            orderTableAfterSit.numberOfGuests shouldBe 0
            orderTableAfterSit.isOccupied shouldBe false
        }

        should("실패 - 해당 주문 테이블의 주문이 완료되지 않은 경우") {
            // given
            val orderTableId = savedOrderTable.id
            every {
                orderRepository.existsByOrderTableAndStatusNot(any(), any<OrderStatus>())
            } returns true

            // when
            val exception = shouldThrowAny {
                service.clear(orderTableId)
            }

            // then
            exception.shouldBeTypeOf<IllegalStateException>()
        }
    }
})
