package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kitchenpos.domain.*
import kitchenpos.infra.PurgomalumClient
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockKExtension::class)
class MenuServiceTest {
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
            val request = Menu()

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("기격이 0원 미만이라면, IllegalArgumentException 예외 처리한다.")
        @Test
        fun test2() {
            // given
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(-1L)
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("존재하지 않는 메뉴 그룹아이디라면, NoSuchElementException 예외 처리한다.")
        @Test
        fun test3() {
            // given
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(1000L)
                every { it.menuGroupId } returns UUID.randomUUID()
            }

            every { menuGroupRepository.findById(any()) } throws NoSuchElementException()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                menuService.create(request)
            }
        }

        @DisplayName("요청의 메뉴 내부 상품이 존재하지 않다면, IllegalArgumentException 예외 처리한다.")
        @Test
        fun test4() {
            // given
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(1000L)
                every { it.menuGroupId } returns UUID.randomUUID()
                every { it.menuProducts } returns null
            }

            every { menuGroupRepository.findById(any()) } returns Optional.of(mockk())

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("요청의 메뉴 내부 상품의 길이가 0이라면, IllegalArgumentException 예외 처리한다.")
        @Test
        fun test5() {
            // given
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(1000L)
                every { it.menuGroupId } returns UUID.randomUUID()
                every { it.menuProducts } returns listOf()
            }

            every { menuGroupRepository.findById(any()) } returns Optional.of(mockk())

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("요청의 메뉴 내부 상품 길이랑, 실제 메뉴 내부 상품 길이랑 다르다면, IllegalArgumentException 예외 처리한다. ")
        @Test
        fun test6() {
            // given
            val menuProduct = mockk<MenuProduct>().also {
                every { it.productId } returns UUID.randomUUID()
            }
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(1000L)
                every { it.menuGroupId } returns UUID.randomUUID()
                every { it.menuProducts } returns listOf(menuProduct)
            }

            every { menuGroupRepository.findById(any()) } returns Optional.of(mockk())
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
            val menuProduct = mockk<MenuProduct>().also {
                every { it.productId } returns UUID.randomUUID()
                every { it.quantity } returns -1
            }
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(1000L)
                every { it.menuGroupId } returns UUID.randomUUID()
                every { it.menuProducts } returns listOf(menuProduct)
            }
            every { menuGroupRepository.findById(any()) } returns Optional.of(mockk())
            every { productRepository.findAllByIdIn(any()) } returns listOf(mockk())

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("요청 내부의 상품이 존재하지 않는 상품이라면, NoSuchElementException 예외 처리")
        @Test
        fun test8() {
            // given
            val menuProduct = mockk<MenuProduct>().also {
                every { it.productId } returns UUID.randomUUID()
                every { it.quantity } returns 1
            }
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(1000L)
                every { it.menuGroupId } returns UUID.randomUUID()
                every { it.menuProducts } returns listOf(menuProduct)
            }
            every { menuGroupRepository.findById(any()) } returns Optional.of(mockk())
            every { productRepository.findAllByIdIn(any()) } returns listOf(mockk())
            every { productRepository.findById(any()) } throws NoSuchElementException()

            // when & then
            shouldThrowExactly<NoSuchElementException> {
                menuService.create(request)
            }
        }

        @DisplayName("요청 내부의 상품이 존재하지 않는 상품이라면, NoSuchElementException 예외 처리")
        @Test
        fun test9() {
            // given
            val menuProduct = mockk<MenuProduct>().also {
                every { it.productId } returns UUID.randomUUID()
                every { it.quantity } returns 1
            }
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(1000L)
                every { it.menuGroupId } returns UUID.randomUUID()
                every { it.menuProducts } returns listOf(menuProduct)
            }
            every { menuGroupRepository.findById(any()) } returns Optional.of(mockk())
            every { productRepository.findAllByIdIn(any()) } returns listOf(mockk())
            every { productRepository.findById(any()) } returns Optional.of(mockk<Product>().also {
                every { it.price } returns BigDecimal.ONE
            })
            every { request.price.compareTo(any()) } returns 1

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("이름 정보가 없다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test10() {
            // given
            val menuProduct = mockk<MenuProduct>().also {
                every { it.productId } returns UUID.randomUUID()
                every { it.quantity } returns 1
            }
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(1000L)
                every { it.menuGroupId } returns UUID.randomUUID()
                every { it.menuProducts } returns listOf(menuProduct)
                every { it.name } returns null
            }
            every { menuGroupRepository.findById(any()) } returns Optional.of(mockk())
            every { productRepository.findAllByIdIn(any()) } returns listOf(mockk())
            every { productRepository.findById(any()) } returns Optional.of(mockk<Product>().also {
                every { it.price } returns BigDecimal.ONE
            })
            every { request.price.compareTo(any()) } returns 0

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuService.create(request)
            }
        }

        @DisplayName("이름에 욕설이 포함되어 있다면, IllegalArgumentException 예외 처리를 한다.")
        @Test
        fun test11() {
            // given
            val menuProduct = mockk<MenuProduct>().also {
                every { it.productId } returns UUID.randomUUID()
                every { it.quantity } returns 1
            }
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(1000L)
                every { it.menuGroupId } returns UUID.randomUUID()
                every { it.menuProducts } returns listOf(menuProduct)
                every { it.name } returns "욕설"
            }
            every { menuGroupRepository.findById(any()) } returns Optional.of(mockk())
            every { productRepository.findAllByIdIn(any()) } returns listOf(mockk())
            every { productRepository.findById(any()) } returns Optional.of(mockk<Product>().also {
                every { it.price } returns BigDecimal.ONE
            })
            every { request.price.compareTo(any()) } returns 0
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
            val menuProduct = mockk<MenuProduct>().also {
                every { it.productId } returns UUID.randomUUID()
                every { it.quantity } returns 1
            }
            val request = mockk<Menu>().also {
                every { it.price } returns BigDecimal.valueOf(1000L)
                every { it.menuGroupId } returns UUID.randomUUID()
                every { it.menuProducts } returns listOf(menuProduct)
                every { it.name } returns "테스트 메뉴"
                every { it.isDisplayed } returns true
                every { it.id } returns UUID.randomUUID()
                every { it.menuGroup } returns mockk()
            }
            every { menuGroupRepository.findById(any()) } returns Optional.of(mockk())
            every { productRepository.findAllByIdIn(any()) } returns listOf(mockk())
            every { productRepository.findById(any()) } returns Optional.of(mockk<Product>().also {
                every { it.price } returns BigDecimal.ONE
            })
            every { request.price.compareTo(any()) } returns 0
            every { purgomalumClient.containsProfanity(any()) } returns false
            every { menuRepository.save(any()) } returns request

            // when
            val result = menuService.create(request)

            // then
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
