package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.mockk
import java.math.BigDecimal
import kitchenpos.domain.Menu
import kitchenpos.domain.Order
import kitchenpos.domain.OrderLineItem
import kitchenpos.domain.OrderStatus
import kitchenpos.domain.OrderTable
import kitchenpos.domain.OrderTableRepository
import kitchenpos.domain.OrderType
import kitchenpos.domain.OrderType.DELIVERY
import kitchenpos.domain.OrderType.EAT_IN
import kitchenpos.domain.OrderType.TAKEOUT
import kitchenpos.infra.KitchenridersClient
import kitchenpos.testsupport.FakeMenuRepository
import kitchenpos.testsupport.FakeOrderRepository
import kitchenpos.testsupport.FakeOrderTableRepository
import kitchenpos.testsupport.MenuFixtures
import kitchenpos.testsupport.OrderTableFixtures
import kitchenpos.testsupport.ProductFixtures

class OrderServiceCreateTest : ShouldSpec({
    lateinit var savedDisplayedMenu: Menu
    lateinit var savedNotDisplayedMenu: Menu
    lateinit var savedOccupiedOrderTable: OrderTable
    lateinit var orderTableRepository: OrderTableRepository
    lateinit var service: OrderService

    beforeTest {
        val menuRepository = FakeMenuRepository()
        orderTableRepository = FakeOrderTableRepository()

        service = OrderService(
            FakeOrderRepository(),
            menuRepository,
            orderTableRepository,
            mockk<KitchenridersClient>()
        )

        savedDisplayedMenu = menuRepository.save(
            MenuFixtures.createMenu(
                product = ProductFixtures.createProduct(),
                name = "test-displayed-menu-name",
                isDisplayed = true
            )
        )

        savedNotDisplayedMenu = menuRepository.save(
            MenuFixtures.createMenu(
                product = ProductFixtures.createProduct(),
                name = "test-not-displayed-menu-name",
                isDisplayed = false
            )
        )

        savedOccupiedOrderTable = orderTableRepository.save(
            OrderTableFixtures.createOrderTable()
                .apply { isOccupied = true }
        )
    }

    context("주문 생성") {
        context("배달 주문") {
            should("성공") {
                // given
                val request = createRequest(
                    type = DELIVERY,
                    menus = listOf(savedDisplayedMenu),
                    deliveryAddress = "test-delivery-address"
                )

                // when
                val order = service.create(request)

                // then
                order.id.shouldNotBeNull()
                order.status shouldBe OrderStatus.WAITING
                order.type shouldBe DELIVERY
                order.deliveryAddress shouldBe request.deliveryAddress
            }

            should("실패 - 주문 수량이 0 미만인 경우") {
                // given
                val request = createRequest(
                    type = DELIVERY,
                    menus = listOf(savedDisplayedMenu),
                    quantity = -1
                )

                // when
                val exception = shouldThrowAny {
                    service.create(request)
                }

                // then
                exception.shouldBeTypeOf<IllegalArgumentException>()
            }

            should("실패 - 배달 주소가 없는 경우") {
                // given
                val request = createRequest(
                    type = DELIVERY,
                    menus = listOf(savedDisplayedMenu),
                    quantity = 1,
                    deliveryAddress = null
                )

                // when
                val exception = shouldThrowAny {
                    service.create(request)
                }

                // then
                exception.shouldBeTypeOf<IllegalArgumentException>()
            }
        }

        context("포장 주문") {
            should("성공") {
                1 shouldBe 1
            }

            should("실패 - 주문 수량이 0 미만인 경우") {
                // given
                val request = createRequest(
                    type = TAKEOUT,
                    menus = listOf(savedDisplayedMenu),
                    quantity = -1
                )

                // when
                val exception = shouldThrowAny {
                    service.create(request)
                }

                // then
                exception.shouldBeTypeOf<IllegalArgumentException>()
            }
        }

        context("매장 주문") {
            should("성공") {
                // given
                val request = createRequest(
                    type = EAT_IN,
                    menus = listOf(savedDisplayedMenu),
                    quantity = 1,
                    orderTable = savedOccupiedOrderTable
                )

                // when
                val order = service.create(request)

                // then
                order.id.shouldNotBeNull()
                order.status shouldBe OrderStatus.WAITING
                order.type shouldBe EAT_IN
            }

            should("성공 - 주문 수량이 0 미만인 경우") {
                // given
                val request = createRequest(
                    type = EAT_IN,
                    menus = listOf(savedDisplayedMenu),
                    quantity = -1,
                    orderTable = savedOccupiedOrderTable
                )

                // when
                val order = service.create(request)

                // then
                order.id.shouldNotBeNull()
                order.status shouldBe OrderStatus.WAITING
                order.type shouldBe EAT_IN
            }

            should("실패 - 착석 처리되지 않은 주문 테이블인 경우") {
                // given
                val notOccupiedOrderTable = orderTableRepository.save(
                    OrderTableFixtures.createOrderTable()
                )
                val request = createRequest(
                    type = EAT_IN,
                    menus = listOf(savedDisplayedMenu),
                    quantity = 1,
                    orderTable = notOccupiedOrderTable
                )

                // when
                val exception = shouldThrowAny {
                    service.create(request)
                }

                // then
                exception.shouldBeTypeOf<IllegalStateException>()
            }
        }

        should("실패 - 주문 방식이 없는 경우") {
            // given
            val request = createRequest(
                type = null,
                menus = listOf(savedDisplayedMenu),
            )

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 주문 상품이 없는 경우") {
            // given
            val request = createRequest(
                type = EAT_IN,
                menus = listOf()
            )

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 주문 메뉴에 존재하지 않는 메뉴가 포함된 경우") {
            // given
            val notExistingMenu = MenuFixtures.createMenu(
                product = ProductFixtures.createProduct(),
                name = "test-not-existing-menu-name",
                isDisplayed = true
            )
            val request = createRequest(
                type = EAT_IN,
                menus = listOf(notExistingMenu)
            )

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 비노출 처리된 메뉴인 경우") {
            // given
            val request = createRequest(
                type = EAT_IN,
                menus = listOf(savedNotDisplayedMenu)
            )

            // when
            val exception = shouldThrowAny {
                service.create(request)
            }

            // then
            exception.shouldBeTypeOf<IllegalStateException>()
        }

        should("실패 - 주문 상품의 가격과 해당 메뉴의 가격이 다른 경우") {
            // given
            val request = createRequest(
                type = EAT_IN,
                menus = listOf(savedDisplayedMenu),
                orderLineItemRequestPrice = savedNotDisplayedMenu.price - 100.toBigDecimal()
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
            type: OrderType? = null,
            menus: List<Menu>,
            orderLineItemRequestPrice: BigDecimal? = null,
            quantity: Long = 0,
            deliveryAddress: String? = null,
            orderTable: OrderTable? = null
        ): Order {
            return Order().apply {
                this.type = type
                this.orderLineItems = menus.map { menu ->
                    OrderLineItem().also {
                        it.menu = menu
                        it.menuId = menu.id
                        it.price = orderLineItemRequestPrice ?: menu.price
                        it.quantity = quantity
                    }
                }
                this.deliveryAddress = deliveryAddress
                this.orderTableId = orderTable?.id
            }
        }
    }
}