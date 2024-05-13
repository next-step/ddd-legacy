package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kitchenpos.domain.FakeOrderRepository
import kitchenpos.domain.FakeOrderTableRepository
import kitchenpos.domain.Order
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTable
import java.util.UUID

private val orderTableRepository = FakeOrderTableRepository()
private val orderRepository = FakeOrderRepository()

private val orderTableService = OrderTableService(orderTableRepository, orderRepository)

private fun createOrderTable(name: String? = "테이블") =
    OrderTable().apply {
        id = UUID.randomUUID()
        this.name = name
        numberOfGuests = 0
    }

class OrderTableServiceTest : BehaviorSpec({
    given("주문 테이블을 생성할 때") {
        `when`("입력 값이 정상이면") {
            val newTable = createOrderTable()

            then("주문 테이블이 생성된다.") {
                with(orderTableService.create(newTable)) {
                    id shouldNotBe null
                    name shouldBe "테이블"
                    numberOfGuests shouldBe 0
                }
            }
        }

        `when`("주문 테이블의 이름이 null이면") {
            val newTable = createOrderTable(name = null)

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    orderTableService.create(newTable)
                }
            }
        }

        `when`("주문 테이블의 이름이 빈 문자열이면") {
            val newTable = createOrderTable(name = "")

            then("예외가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    orderTableService.create(newTable)
                }
            }
        }
    }

    given("주문 테이블에 손님이 않을 떄") {
        `when`("주문 테이블이 존재하면") {
            val newTable = createOrderTable().let { orderTableService.create(it) }

            then("손님이 앉을 수 있다.") {
                with(orderTableService.sit(newTable.id)) {
                    isOccupied shouldBe true
                }
            }
        }

        `when`("주문 테이블이 존재하지 않으면") {
            val newTable = createOrderTable()

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    orderTableService.sit(newTable.id)
                }
            }
        }
    }

    given("주문 테이블을 정리할 때") {
        `when`("주문 테이블이 존재하고 주문 상태가 완료되었으면") {
            val newTable = orderTableService.create(createOrderTable())
            val completedOrder =
                Order().apply {
                    orderTableId = newTable.id
                    status = OrderStatus.COMPLETED
                }

            orderRepository.save(completedOrder)

            then("주문 테이블이 정리된다.") {
                with(orderTableService.clear(newTable.id)) {
                    numberOfGuests shouldBe 0
                    isOccupied shouldBe false
                }
            }
        }

        `when`("주문 테이블이 존재하지 않으면") {
            val newTable = createOrderTable()

            then("예외가 발생한다.") {
                shouldThrow<NoSuchElementException> {
                    orderTableService.clear(newTable.id)
                }
            }
        }

        `when`("주문이 아직 완료되지 않았으면") {
            val newTable = orderTableService.create(createOrderTable())
            val uncompletedOrder =
                Order().apply {
                    orderTableId = newTable.id
                    status = OrderStatus.SERVED
                }

            orderRepository.save(uncompletedOrder)

            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    orderTableService.clear(newTable.id)
                }
            }
        }
    }
})
