package kitchenpos.application

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import kitchenpos.testsupport.FakeOrderRepository
import kitchenpos.testsupport.FakeOrderTableRepository
import kitchenpos.testsupport.OrderTableFixtures

class OrderTableServiceSitTest : ShouldSpec({
    lateinit var savedOrderTable: OrderTable
    lateinit var orderTableRepository: OrderTableRepository
    lateinit var service: OrderTableService

    beforeTest {
        orderTableRepository = FakeOrderTableRepository()
        service = OrderTableService(
            orderTableRepository,
            FakeOrderRepository()
        )

        savedOrderTable = orderTableRepository.save(
            OrderTableFixtures.createOrderTable()
        )
    }

    context("주문 테이블 착석 처리") {
        should("성공") {
            // given
            val orderTableId = savedOrderTable.id
            val isOccupiedBeforeSit = savedOrderTable.isOccupied

            // when
            val orderTableAfterSit = service.sit(orderTableId)

            // then
            isOccupiedBeforeSit shouldBe false
            orderTableAfterSit.isOccupied shouldBe true
        }
    }
})
