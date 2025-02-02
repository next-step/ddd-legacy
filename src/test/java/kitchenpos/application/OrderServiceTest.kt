package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.transaction.Transactional
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.Order
import kitchenpos.domain.OrderLineItem
import kitchenpos.domain.OrderRepository
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import kitchenpos.domain.OrderType
import kitchenpos.infra.KitchenridersClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
@Transactional
class OrderServiceTest : BehaviorSpec() {
    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var orderTableRepository: OrderTableRepository

    @Autowired
    private lateinit var menuRepository: MenuRepository

    @Autowired
    private lateinit var kitchenridersClient: KitchenridersClient

    @Autowired
    private lateinit var createMenuHelper: CreateMenuHelper

    init {

        Given("주문을 등록할 때") {
            val menu = createMenuHelper.createTestMenu(
                menuName = "메뉴",
                menuPrice = BigDecimal("10000"),
                menuGroupName = "메뉴 그룹",
                productName = "상품",
                productPrice = BigDecimal("10000"),
                productQuantity = 1L,
                isDisplayed = true
            )
            menuRepository.save(menu)

            When("필수값이 모두 올바르게 입력된 경우") {
                val orderLineItems = listOf(
                    OrderLineItem().apply {
                        menuId = menu.id
                        quantity = 1
                        price = menu.price
                    }
                )

                val order = Order().apply {
                    type = OrderType.TAKEOUT
                    this.orderLineItems = orderLineItems
                }

                Then("주문이 정상적으로 등록된다") {
                    val savedOrder = orderService.create(order)

                    savedOrder.id shouldNotBe null
                    savedOrder.status shouldBe OrderStatus.WAITING
                    savedOrder.orderLineItems shouldHaveSize 1
                }
            }

            When("주문 타입이 null인 경우") {
                val order = Order().apply {
                    type = null
                    orderLineItems = listOf()
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        orderService.create(order)
                    }
                }
            }

            When("주문 내역이 비어있는 경우") {
                val order = Order().apply {
                    type = OrderType.TAKEOUT
                    orderLineItems = listOf()
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalArgumentException> {
                        orderService.create(order)
                    }
                }
            }

            When("메뉴가 숨김 처리된 경우") {
                val hiddenMenu = createMenuHelper.createTestMenu(
                    menuName = "숨김메뉴",
                    menuPrice = BigDecimal("10000"),
                    menuGroupName = "메뉴 그룹",
                    productName = "상품",
                    productPrice = BigDecimal("10000"),
                    productQuantity = 1L,
                    isDisplayed = false
                )
                menuRepository.save(hiddenMenu)

                val orderLineItems = listOf(
                    OrderLineItem().apply {
                        menuId = hiddenMenu.id
                        quantity = 1
                        price = hiddenMenu.price
                    }
                )

                val order = Order().apply {
                    type = OrderType.TAKEOUT
                    this.orderLineItems = orderLineItems
                }

                Then("예외가 발생한다") {
                    shouldThrow<IllegalStateException> {
                        orderService.create(order)
                    }
                }
            }
        }

        Given("배달 주문을 처리할 때") {
            val menu = createMenuHelper.createTestMenu(
                menuName = "메뉴",
                menuPrice = BigDecimal("10000"),
                menuGroupName = "메뉴 그룹",
                productName = "상품",
                productPrice = BigDecimal("10000"),
                productQuantity = 1L,
                isDisplayed = true
            )
            menuRepository.save(menu)

            val orderLineItems = listOf(
                OrderLineItem().apply {
                    this.menu = menu
                    quantity = 1
                    price = menu.price
                }
            )

            val order = Order().apply {
                id = UUID.randomUUID()
                type = OrderType.DELIVERY
                status = OrderStatus.WAITING
                deliveryAddress = "서울시 강남구"
                this.orderLineItems = orderLineItems
                orderDateTime = LocalDateTime.now()
            }
            orderRepository.save(order)

            When("주문을 승인하면") {
                Then("주문 상태가 ACCEPTED로 변경되고 배달 요청이 발생한다") {
                    val acceptedOrder = orderService.accept(order.id!!)

                    acceptedOrder.status shouldBe OrderStatus.ACCEPTED
                }
            }

            When("배달 시작 시") {
                val servedOrder = order.apply { status = OrderStatus.SERVED }
                orderRepository.save(servedOrder)

                Then("주문 상태가 DELIVERING으로 변경된다") {
                    val deliveringOrder = orderService.startDelivery(servedOrder.id!!)
                    deliveringOrder.status shouldBe OrderStatus.DELIVERING
                }
            }
        }

        Given("매장 식사 주문을 처리할 때") {
            val orderTable = OrderTable().apply {
                id = UUID.randomUUID()
                numberOfGuests = 4
                isOccupied = true
                name = "1번 테이블"
            }
            orderTableRepository.save(orderTable)

            val menu = createMenuHelper.createTestMenu(
                menuName = "메뉴",
                menuPrice = BigDecimal("10000"),
                menuGroupName = "메뉴 그룹",
                productName = "상품",
                productPrice = BigDecimal("10000"),
                productQuantity = 1L,
                isDisplayed = true
            )
            menuRepository.save(menu)

            val orderLineItems = listOf(
                OrderLineItem().apply {
                    this.menu = menu
                    quantity = 1
                    price = menu.price
                }
            )

            val order = Order().apply {
                id = UUID.randomUUID()
                type = OrderType.EAT_IN
                status = OrderStatus.SERVED
                this.orderTable = orderTable
                this.orderLineItems = orderLineItems
                orderDateTime = LocalDateTime.now()
            }
            orderRepository.save(order)

            When("주문을 완료하면") {
                Then("주문 상태가 COMPLETED로 변경되고 테이블이 비워진다") {
                    val completedOrder = orderService.complete(order.id!!)

                    completedOrder.status shouldBe OrderStatus.COMPLETED
                    completedOrder.orderTable?.isOccupied shouldBe false
                    completedOrder.orderTable?.numberOfGuests shouldBe 0
                }
            }
        }
    }
}
