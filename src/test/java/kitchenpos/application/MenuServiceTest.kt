package kitchenpos.application

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kitchenpos.domain.*
import kitchenpos.infra.PurgomalumClient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockKExtension::class)
internal class MenuServiceTest {
    @MockK
    private lateinit var menuRepository: MenuRepository

    @MockK
    private lateinit var menuGroupRepository: MenuGroupRepository

    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var purgomalumClient: PurgomalumClient

    @InjectMockKs
    private lateinit var menuService: MenuService

    @Nested
    inner class `메뉴 생성 테스트` {
        @DisplayName("가격 정보가 없으면, IllegalArgumentException 예외 처리한다.")
        @Test
        fun test1() {
            // given
            val request = createMenuRequest(price = null,)

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("기격이 0원 미만이라면, IllegalArgumentException 예외 처리한다.")
        @Test
        fun test2() {
            // given
            val request = createMenuRequest(price = BigDecimal.valueOf(-1L),)


            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("존재하지 않는 메뉴 그룹아이디라면, NoSuchElementException 예외 처리한다.")
        @Test
        fun test3() {
            // given
            val request = createMenuRequest()

            every { menuGroupRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                menuService.create(request)
            }
        }

        @DisplayName("요청의 메뉴 내부 상품이 존재하지 않다면, IllegalArgumentException 예외 처리한다.")
        @Test
        fun test4() {
            // given
            val request = createMenuRequest(menuProducts = null,)


            every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("요청의 메뉴 내부 상품의 길이가 0이라면, IllegalArgumentException 예외 처리한다.")
        @Test
        fun test5() {
            // given
            val request = createMenuRequest(menuProducts = emptyList(),)


            every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("요청의 메뉴 내부 상품 길이랑, 실제 메뉴 내부 상품 길이랑 다르다면, IllegalArgumentException 예외 처리한다. ")
        @Test
        fun test6() {
            // given
            val request = createMenuRequest(
                menuProducts = listOf(
                    createMenuProduct(
                        product = createProduct(id = UUID.randomUUID()),
                        quantity = 1,
                    ),
                ),
            )

            every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())
            every { productRepository.findAllByIdIn(any()) } returns listOf()

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("요청의 메뉴 내부 상품의 수량이 음수라면, IllegalArgumentException 예외 처리")
        @Test
        fun test7() {
            // given
            val request = createMenuRequest(
                menuProducts = listOf(
                    createMenuProduct(
                        product = createProduct(id = UUID.randomUUID()),
                        quantity = -1,
                    ),
                ),
            )

            every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())
            every { productRepository.findAllByIdIn(any()) } returns listOf(Product())

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("요청 내부의 상품이 존재하지 않는 상품이라면, NoSuchElementException 예외 처리")
        @Test
        fun test9() {
            // given
            val request = createMenuRequest()

            every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())
            every { productRepository.findAllByIdIn(any()) } returns listOf(Product())
            every { productRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                menuService.create(request)
            }
        }

        @DisplayName("이름 정보가 없다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test10() {
            // given
            val request = createMenuRequest(name = null,)

            every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())
            every { productRepository.findAllByIdIn(any()) } returns listOf(Product())
            every { productRepository.findById(any()) } returns Optional.of(Product().apply {
                this.price = BigDecimal.TWO
            })

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("이름에 욕설이 포함되어 있다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test11() {
            // given
            val request = createMenuRequest(name = "욕설",)

            every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())
            every { productRepository.findAllByIdIn(any()) } returns listOf(Product())
            every { productRepository.findById(any()) } returns Optional.of(Product().apply {
                this.price = BigDecimal.TWO
            })
            every { purgomalumClient.containsProfanity(any()) } returns true

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("정상 조건이라면, 메뉴를 저장한다.")
        @Test
        fun test12() {
            // given
            val request = createMenuRequest(
                name = "메뉴 이름",
                isDisplayed = true,
                price = BigDecimal.ONE,
                menuProducts = listOf(
                    createMenuProduct(
                        product = createProduct(id = UUID.randomUUID()),
                        quantity = 1,
                    ),
                ),
            )

            every { menuGroupRepository.findById(any()) } returns Optional.of(MenuGroup())
            every { productRepository.findAllByIdIn(any()) } returns listOf(Product())
            every { productRepository.findById(any()) } returns Optional.of(Product().apply {
                this.price = BigDecimal.TWO
            })

            every { purgomalumClient.containsProfanity(any()) } returns false
            every { menuRepository.save(any()) } returns request

            // when
            val result = menuService.create(request)

            // then
            assertSoftly {
                verify(exactly = 1) { menuRepository.save(any()) }
                result.id shouldBe request.id
                result.name shouldBe request.name
                result.price shouldBe request.price
                result.menuGroup shouldBe request.menuGroup
                result.isDisplayed shouldBe request.isDisplayed
                result.menuProducts shouldBe request.menuProducts
            }
        }
    }

    @Nested
    inner class `메뉴 가격 변경 테스트` {
        @DisplayName("가격 정보가 null 이면, IllegalArgumentException 예외 처리한다.")
        @Test
        fun test1() {
            // given
            val menuId = UUID.randomUUID()
            val request = createMenuRequest(price = null)

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.changePrice(menuId, request)
            }
        }

        @DisplayName("가격 정보가 0보다 작으면, IllegalArgumentException 예외 처리 한다.")
        @Test
        fun test2() {
            // given
            val menuId = UUID.randomUUID()
            val request = createMenuRequest(price = BigDecimal.valueOf(-1L))

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.changePrice(menuId, request)
            }
        }

        @DisplayName("메뉴 id가 존재하지 않으면, NoSuchElementException 예외 처리한다.")
        @Test
        fun test3() {
            // given
            val menuId = UUID.randomUUID()
            val request = createMenuRequest(price = BigDecimal.valueOf(1000L))

            every { menuRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                menuService.changePrice(menuId, request)
            }
        }

        @DisplayName("메뉴에 속한 상품들의 가격보다 메뉴의 가격이 더 크면, IllegalArgumentException 예외 처리한다.")
        @Test
        fun test4() {
            // given
            val menuId = UUID.randomUUID()
            val request = createMenuRequest(
                price = BigDecimal.valueOf(1001L),
                menuProducts = listOf(
                    createMenuProduct(
                        product = createProduct(price = BigDecimal.valueOf(1000L)), quantity = 1
                    )
                ),
            )

            every { menuRepository.findById(any()) } returns Optional.of(request)

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.changePrice(menuId, request)
            }
        }

        @DisplayName("메뉴 가격 변경 요청이 정상이라면, 가격 변경")
        @Test
        fun test5() {
            // given
            val menuId = UUID.randomUUID()
            val request = createMenuRequest(
                price = BigDecimal.valueOf(1000L),
                menuProducts = listOf(
                    createMenuProduct(
                        product = createProduct(price = BigDecimal.valueOf(1001L)), quantity = 1
                    )
                ),
            )

            val 찾아온_메뉴 = createMenu(
                id = menuId,
                price = request.price,
                menuProducts = request.menuProducts,
                isDisplayed = request.isDisplayed
            )

            createMenuRequest(
                price = BigDecimal.valueOf(1000L),
                menuProducts = listOf(
                    createMenuProduct(
                        product = createProduct(price = BigDecimal.valueOf(1001L)), quantity = 1
                    )
                ),
            )

            every { menuRepository.findById(any()) } returns Optional.of(찾아온_메뉴)

            // when
            val result = menuService.changePrice(menuId, request)

            // then
            result.price shouldBe request.price
        }
    }

    @Nested
    inner class `메뉴 노출 테스트` {
        @DisplayName("요청한 메뉴 id가 존재하지 않다면, NoSuchElementException 예외 처리")
        @Test
        fun test1() {
            // given
            val menuId = UUID.randomUUID()

            every { menuRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                menuService.display(menuId)
            }
        }

        @DisplayName("메뉴에 속한 상품들의 가격 합보다, 메뉴의 가격이 더 높으면 IllegalStateException 예외 처리한다.")
        @Test
        fun test2() {
            // given
            val menuId = UUID.randomUUID()
            val menu = createMenu(
                id = menuId, price = BigDecimal.valueOf(1001L), menuProducts = listOf(
                    createMenuProduct(
                        product = createProduct(price = BigDecimal.valueOf(1000L)),
                        quantity = 1,
                    )
                ), isDisplayed = false
            )

            every { menuRepository.findById(any()) } returns Optional.of(menu)

            // when & then
            shouldThrowExactly<IllegalStateException> {
                menuService.display(menuId)
            }
        }

        @DisplayName("메뉴에 속한 상품들의 가격 합보다, 메뉴의 가격이 더 낮으면, 메뉴를 노출한다.")
        @Test
        fun test3() {
            // given
            val menuId = UUID.randomUUID()
            val menu = createMenu(
                id = menuId, price = BigDecimal.valueOf(999L), menuProducts = listOf(
                    createMenuProduct(
                        product = createProduct(price = BigDecimal.valueOf(1000L)),
                        quantity = 1,
                    )
                ), isDisplayed = false
            )

            every { menuRepository.findById(any()) } returns Optional.of(menu)

            // when
            val result = menuService.display(menuId)

            //then
            result.isDisplayed shouldBe true
        }
    }

    @Nested
    inner class `메뉴 숨김 테스트` {
        @DisplayName("존재하지 않는 메뉴 id 라면, NoSuchElementException 예외 처리한다.")
        @Test
        fun test1() {
            // given
            val menuId = UUID.randomUUID()

            every { menuRepository.findById(any()) } returns Optional.empty()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                menuService.hide(menuId)
            }
        }

        @DisplayName("정상 요청이라면, 메뉴를 숨긴다.")
        @ParameterizedTest
        @ValueSource(booleans = [false, true])
        fun test2(initDisplayed: Boolean) {
            // given
            val menuId = UUID.randomUUID()

            val 찾아온_메뉴 = createMenu(
                id = menuId,
            )

            every { menuRepository.findById(any()) } returns Optional.of(찾아온_메뉴)

            // when
            val result = menuService.hide(menuId)

            // then
            result.isDisplayed shouldBe false
        }
    }

    private fun createMenuRequest(
        id: UUID = UUID.randomUUID(),
        isDisplayed: Boolean = true,
        price: BigDecimal? = BigDecimal.valueOf(1000L),
        menuProducts: List<MenuProduct>? = listOf(createMenuProduct()),
        menuGroup: MenuGroup? = MenuGroup(),
        name: String? = "메뉴 이름",
    ) = createMenu(
        id = id,
        isDisplayed = isDisplayed,
        price = price,
        menuProducts = menuProducts,
        menuGroup = menuGroup,
        name = name,
    )

    private fun createMenuProduct(
        seq: Long = 1L,
        product: Product = Product(),
        quantity: Long = 1,
    ) = MenuProduct().apply {
        this.seq = seq
        this.productId = product.id
        this.product = product
        this.quantity = quantity
    }

    private fun createMenu(
        id: UUID = UUID.randomUUID(),
        price: BigDecimal? = BigDecimal.valueOf(1000L),
        menuProducts: List<MenuProduct>? = listOf(createMenuProduct()),
        isDisplayed: Boolean = true,
        menuGroup: MenuGroup? = MenuGroup(),
        name: String? = "메뉴 이름",
    ) = Menu().apply {
        this.id = id
        this.price = price
        this.menuProducts = menuProducts
        this.isDisplayed = isDisplayed
        this.name = name
        this.menuGroup = menuGroup
    }

    private fun createProduct(
        id: UUID = UUID.randomUUID(),
        price: BigDecimal = BigDecimal.valueOf(1000L),
    ) = Product().apply {
        this.id = id
        this.price = price
    }
}
