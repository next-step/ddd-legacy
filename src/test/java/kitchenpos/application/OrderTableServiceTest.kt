package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.transaction.Transactional
import kitchenpos.domain.Order
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import kitchenpos.domain.OrderType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
@Transactional
class OrderTableServiceTest : BehaviorSpec() {
    @Autowired
    private lateinit var orderTableService: OrderTableService

    @Autowired
    private lateinit var orderTableRepository: OrderTableRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    init {
        Given("테이블을 생성할 때") {
            When("올바른 테이블 이름으로 생성하는 경우") {
                val orderTable = OrderTable().apply {
                    name = "1번 테이블"
                }

                Then("테이블이 정상적으로 생성된다") {
                    val savedTable = orderTableService.create(orderTable)

                    savedTable.id shouldNotBe null
                    savedTable.name shouldBe "1번 테이블"
                    savedTable.numberOfGuests shouldBe 0
                    savedTable.isOccupied shouldBe false
                }
            }

            When("테이블 이름이 null인 경우") {
                val orderTable = OrderTable().apply {
                    name = null
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        orderTableService.create(orderTable)
                    }
                }
            }

            When("테이블 이름이 빈 문자열인 경우") {
                val orderTable = OrderTable().apply {
                    name = ""
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        orderTableService.create(orderTable)
                    }
                }
            }
        }

        Given("테이블에 손님을 착석시킬 때") {
            val orderTable = OrderTable().apply {
                id = UUID.randomUUID()
                name = "1번 테이블"
                numberOfGuests = 0
                isOccupied = false
            }
            orderTableRepository.save(orderTable)

            When("존재하는 테이블에 착석시키는 경우") {
                Then("테이블이 점유 상태로 변경된다") {
                    val occupiedTable = orderTableService.sit(orderTable.id!!)
                    occupiedTable.isOccupied shouldBe true
                }
            }

            When("존재하지 않는 테이블에 착석시키는 경우") {
                Then("예외가 발생한다") {
                    shouldThrow<NoSuchElementException> {
                        orderTableService.sit(UUID.randomUUID())
                    }
                }
            }
        }

        Given("테이블을 비울 때") {
            val orderTable = OrderTable().apply {
                id = UUID.randomUUID()
                name = "1번 테이블"
                numberOfGuests = 4
                isOccupied = true
            }
            orderTableRepository.save(orderTable)

            When("완료되지 않은 주문이 없는 경우") {
                Then("테이블이 정상적으로 비워진다") {
                    val clearedTable = orderTableService.clear(orderTable.id!!)

                    clearedTable.isOccupied shouldBe false
                    clearedTable.numberOfGuests shouldBe 0
                }
            }

            When("완료되지 않은 주문이 있는 경우") {
                val order = Order().apply {
                    id = UUID.randomUUID()
                    type = OrderType.EAT_IN
                    status = OrderStatus.ACCEPTED
                    this.orderTable = orderTable
                    orderDateTime = LocalDateTime.now()
                }
                orderRepository.save(order)

                Then("예외가 발생한다") {
                    shouldThrow<IllegalStateException> {
                        orderTableService.clear(orderTable.id!!)
                    }
                }
            }

            When("존재하지 않는 테이블을 비우려는 경우") {
                Then("예외가 발생한다") {
                    shouldThrow<NoSuchElementException> {
                        orderTableService.clear(UUID.randomUUID())
                    }
                }
            }
        }

        Given("테이블의 손님 수를 변경할 때") {
            val orderTable = OrderTable().apply {
                id = UUID.randomUUID()
                name = "1번 테이블"
                numberOfGuests = 4
                isOccupied = true
            }
            orderTableRepository.save(orderTable)

            When("올바른 손님 수로 변경하는 경우") {
                val request = OrderTable().apply {
                    numberOfGuests = 6
                }

                Then("손님 수가 정상적으로 변경된다") {
                    val updatedTable = orderTableService.changeNumberOfGuests(orderTable.id!!, request)
                    updatedTable.numberOfGuests shouldBe 6
                }
            }

            When("손님 수가 음수인 경우") {
                val request = OrderTable().apply {
                    numberOfGuests = -1
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        orderTableService.changeNumberOfGuests(orderTable.id!!, request)
                    }
                }
            }

            When("비어있는 테이블의 손님 수를 변경하는 경우") {
                val emptyTable = OrderTable().apply {
                    id = UUID.randomUUID()
                    name = "2번 테이블"
                    numberOfGuests = 0
                    isOccupied = false
                }
                orderTableRepository.save(emptyTable)

                val request = OrderTable().apply {
                    numberOfGuests = 4
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalStateException> {
                        orderTableService.changeNumberOfGuests(emptyTable.id!!, request)
                    }
                }
            }

            When("존재하지 않는 테이블의 손님 수를 변경하는 경우") {
                val request = OrderTable().apply {
                    numberOfGuests = 4
                }

                Then("예외가 발생한다") {
                    shouldThrow<NoSuchElementException> {
                        orderTableService.changeNumberOfGuests(UUID.randomUUID(), request)
                    }
                }
            }
        }
    }
}
