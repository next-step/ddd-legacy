package kitchenpos.application

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.MenuRepository
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import random.randomMenu
import random.randomMenuId
import random.randomMenuProduct
import random.randomProduct
import java.math.BigDecimal
import java.util.*

class MenuServiceTest {
    private val menuRepository: MenuRepository = mockk()
    private val menuGroupRepository: MenuGroupRepository = mockk()
    private val productRepository: ProductRepository = mockk()
    private val purgomalumClient: PurgomalumClient = mockk()
    private lateinit var menuService: MenuService

    @BeforeEach
    fun setUp() {
        menuService = MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient)
    }

    @Nested
    inner class `메뉴 생성시` {

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = [-1])
        fun `가격은 필수값이며, 0보다 커야한다 아닌 경우 예외 발생`(input: Long?) {
            // given
            val menu = randomMenu(price = input?.let { BigDecimal.valueOf(it) })
            // when then
            assertThrows<IllegalArgumentException> {
                menuService.create(menu)
            }
        }

        @Test
        fun `생성하려는 하는 메뉴의 그룹이 존재해야한다 아닌 경우 예외 발생`() {
            // given
            val menu = randomMenu()
            every { menuGroupRepository.findById(menu.menuGroupId) } returns Optional.empty()

            // when then
            assertThrows<NoSuchElementException> {
                menuService.create(menu)
            }
        }

        @Test
        fun `메뉴 상품은 필수값이며, 최소 1개 이상이어야 한다 아닌 경우 예외 발생`() {
            // given
            val menu = randomMenu(menuProducts = emptyList())
            every { menuGroupRepository.findById(menu.menuGroupId) } returns Optional.of(menu.menuGroup)

            // when then
            assertThrows<IllegalArgumentException> {
                menuService.create(menu)
            }
        }

        @Test
        fun `메뉴 상품은 반드시 상품 테이블에 존재해야 한다 아닌 경우 에러 발생`() {
            // given
            val menuProducts = listOf(randomMenuProduct(), randomMenuProduct())
            val menu = randomMenu(menuProducts = menuProducts)
            every { menuGroupRepository.findById(menu.menuGroupId) } returns Optional.of(menu.menuGroup)
            every { productRepository.findAllByIdIn(menuProducts.map(MenuProduct::getProductId)) } returns emptyList()

            // when then
            assertThrows<IllegalArgumentException> {
                menuService.create(menu)
            }
        }

        @ParameterizedTest
        @ValueSource(longs = [-1, -2])
        fun `메뉴 상품의 수량은 0보다 커야한다 아닌 경우 예외 발생`(input: Long) {
            // given
            val menuProducts = listOf(randomMenuProduct(quantity = input), randomMenuProduct(quantity = input))
            val menu = randomMenu(menuProducts = menuProducts)
            every { menuGroupRepository.findById(menu.menuGroupId) } returns Optional.of(menu.menuGroup)
            every { productRepository.findAllByIdIn(menuProducts.map(MenuProduct::getProductId)) } returns listOf(
                randomProduct(),
                randomProduct()
            )

            // when then
            assertThrows<IllegalArgumentException> {
                menuService.create(menu)
            }
        }

        @Test
        fun `메뉴 가격이 메뉴 상품들의 합산 가격보다 크지 않아야 한다 아닌 경우 에러 발생`() {
            // given
            val product = randomProduct(price = BigDecimal.valueOf(100))
            val menuProducts = listOf(randomMenuProduct(quantity = 1, product = product))
            val menu = randomMenu(menuProducts = menuProducts, price = BigDecimal.valueOf(250))
            every { menuGroupRepository.findById(menu.menuGroupId) } returns Optional.of(menu.menuGroup)
            every { productRepository.findAllByIdIn(menuProducts.map(MenuProduct::getProductId)) } returns listOf(
                randomProduct(),
                randomProduct()
            )
            every { productRepository.findById(any()) } returns Optional.of(product)

            // when then
            assertThrows<IllegalArgumentException> {
                menuService.create(menu)
            }
        }

        @Test
        fun `메뉴명은 필수값이다 아닐 경우 에러 발생`() {
            // given
            val product = randomProduct(price = BigDecimal.valueOf(100))
            val menuProducts = listOf(randomMenuProduct(quantity = 1, product = product))
            val menu = randomMenu(menuProducts = menuProducts, price = BigDecimal.valueOf(80), name = null)
            every { menuGroupRepository.findById(menu.menuGroupId) } returns Optional.of(menu.menuGroup)
            every { productRepository.findAllByIdIn(menuProducts.map(MenuProduct::getProductId)) } returns listOf(
                randomProduct(),
                randomProduct()
            )
            every { productRepository.findById(any()) } returns Optional.of(product)

            // when then
            assertThrows<IllegalArgumentException> {
                menuService.create(menu)
            }
        }

        @Test
        fun `메뉴명은 비속어가 포함되지 않아야 한다 아닐 경우 에러 발생`() {
            // given
            val product = randomProduct(price = BigDecimal.valueOf(100))
            val menuProducts = listOf(randomMenuProduct(quantity = 1, product = product))
            val menu = randomMenu(menuProducts = menuProducts, price = BigDecimal.valueOf(80))
            every { menuGroupRepository.findById(menu.menuGroupId) } returns Optional.of(menu.menuGroup)
            every { productRepository.findAllByIdIn(menuProducts.map(MenuProduct::getProductId)) } returns listOf(
                randomProduct(),
                randomProduct()
            )
            every { productRepository.findById(any()) } returns Optional.of(product)
            every { purgomalumClient.containsProfanity(menu.name) } returns true

            // when then
            assertThrows<IllegalArgumentException> {
                menuService.create(menu)
            }
        }
    }

    @Nested
    inner class `메뉴 수정시` {

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = [-1])
        fun `가격은 필수값이며, 0보다 커야한다 아닌 경우 예외 발생`(input: Long?) {
            // given
            val menu = randomMenu(price = input?.let { BigDecimal.valueOf(it) })
            // when then
            assertThrows<IllegalArgumentException> {
                menuService.changePrice(randomMenuId(), menu)
            }
        }

        @Test
        fun `수정하려는 메뉴가 존재해야 한다 아닌 경우 예외 발생`() {
            // given
            val menu = randomMenu()
            every { menuRepository.findById(menu.id) } returns Optional.empty()

            // when then
            assertThrows<NoSuchElementException> {
                menuService.changePrice(menu.id, menu)
            }
        }

        @Test
        fun `수정된 가격이 메뉴 상품들의 합산 가격보다 크지 않아야 한다 아닌 경우 예외 발생`() {
            // given
            val menu = randomMenu(price = BigDecimal.valueOf(1000))
            every { menuRepository.findById(menu.id) } returns Optional.of(
                randomMenu(
                    menuProducts = listOf(
                        randomMenuProduct(product = randomProduct(price = BigDecimal.valueOf(100)), quantity = 1)
                    )
                )
            )

            // when then
            assertThrows<IllegalArgumentException> {
                menuService.changePrice(menu.id, menu)
            }
        }

        @Test
        fun `가격 정상 변경시, 변경된 가격 반환한다`() {
            // given
            val expectedPrice = BigDecimal.valueOf(100)
            val menu = randomMenu(price = expectedPrice)
            every { menuRepository.findById(menu.id) } returns Optional.of(
                randomMenu(
                    menuProducts = listOf(
                        randomMenuProduct(product = randomProduct(price = BigDecimal.valueOf(100)), quantity = 1)
                    )
                )
            )
            // when
            val result = menuService.changePrice(menu.id, menu)
            // then
            result.price shouldBe expectedPrice
        }
    }

    @Nested
    inner class `메뉴 노출` {

        @Test
        fun `노출하려는 메뉴가 존재해야 한다 아닌 경우 예외 발생`() {
            // given
            val menuId = randomMenuId()
            every { menuRepository.findById(menuId) } returns Optional.empty()

            // when then
            assertThrows<NoSuchElementException> {
                menuService.display(menuId)
            }
        }

        @Test
        fun `메뉴 가격이 메뉴 상품들의 합산 가격보다 크지 않아야 한다 아닌 경우 예외 발생`() {
            val menuId = randomMenuId()
            every { menuRepository.findById(menuId) } returns Optional.of(
                randomMenu(
                    id = menuId,
                    price = BigDecimal.valueOf(1000),
                    menuProducts = listOf(
                        randomMenuProduct(product = randomProduct(price = BigDecimal.valueOf(100)), quantity = 1)
                    )
                )
            )

            // when then
            assertThrows<IllegalStateException> {
                menuService.display(menuId)
            }
        }

        @Test
        fun `정상 노출 반환`() {
            // given
            val menuId = randomMenuId()
            every { menuRepository.findById(menuId) } returns Optional.of(randomMenu(isDisplayed = false, price = BigDecimal.ZERO))

            // when
            val result = menuService.display(menuId)

            // then
            result.isDisplayed shouldBe true
        }
    }

    @Nested
    inner class `메뉴 비노출` {

        @Test
        fun `비노출하려는 메뉴가 존재해야 한다 아닌 경우 예외 발생`() {
            // given
            val menuId = randomMenuId()
            every { menuRepository.findById(menuId) } returns Optional.empty()

            // when then
            assertThrows<NoSuchElementException> {
                menuService.hide(menuId)
            }
        }

        @Test
        fun `정상 비노출 반환`() {
            // given
            val menuId = randomMenuId()
            every { menuRepository.findById(menuId) } returns Optional.of(randomMenu(isDisplayed = true))

            // when
            val result = menuService.hide(menuId)

            // then
            result.isDisplayed shouldBe false
        }
    }

    @Test
    fun `메뉴 조회`() {
        val expectedMenus = listOf(randomMenu(), randomMenu())
        every { menuRepository.findAll() } returns expectedMenus

        menuService.findAll() shouldBe expectedMenus
    }
}
