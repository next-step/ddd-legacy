package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kitchenpos.domain.OrderTable
import kitchenpos.testsupport.FakeOrderRepository
import kitchenpos.testsupport.FakeOrderTableRepository

class OrderTableServiceCreateTest : ShouldSpec({
    lateinit var service: OrderTableService

    beforeTest {
        service = OrderTableService(
            FakeOrderTableRepository(),
            FakeOrderRepository()
        )
    }

    context("주문 테이블 생성") {
        should("성공") {
            // given
            val request = createRequest()

            // when
            val orderTable = service.create(request)

            // then
            orderTable.id.shouldNotBeNull()
            orderTable.name shouldBe request.name
            orderTable.numberOfGuests shouldBe 0
            orderTable.isOccupied shouldBe false
        }

        should("실패 - 그룹명이 비어있는 경우") {
            // given
            val request = createRequest(
                name = null
            )

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }
    }
}) {
    companion object {
        private fun createRequest(
            name: String? = "test-order-table-name"
        ): OrderTable {
            return OrderTable().apply {
                this.name = name
            }
        }
    }
}