package kitchenpos.application

import kitchenpos.domain.*
import kitchenpos.infra.PurgomalumClient
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import utils.FakeMenuRepository
import utils.spec.MenuGroupSpec
import utils.spec.MenuProductSpec
import utils.spec.MenuSpec
import utils.spec.ProductSpec
import java.math.BigDecimal
import java.util.*

@DisplayName("메뉴 서비스")
@ExtendWith(MockitoExtension::class)
class MenuServiceMockTest {
    private val menuGroupRepository = mock(MenuGroupRepository::class.java)
    private val productRepository = mock(ProductRepository::class.java)
    private val purgomalumClient = mock(PurgomalumClient::class.java)

    private val menuService: MenuService =
        MenuService(FakeMenuRepository, menuGroupRepository, productRepository, purgomalumClient)

    private val productService: ProductService =
        ProductService(productRepository, FakeMenuRepository, purgomalumClient)

    @Nested
    @DisplayName("메뉴 생성")
    inner class `메뉴 생성` {
        @Test
        fun `메뉴 생성 요청 - 정상적인 메뉴 생성 성공`() {
            val menuGroup = MenuGroupSpec.of()
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)

            val request = Menu()
            request.name = "치킨"
            request.price = BigDecimal.valueOf(9000)
            request.menuProducts = listOf(menuProduct)
            request.isDisplayed = true
            request.menuGroupId = menuGroup.id

            `when`(menuGroupRepository.findById(menuGroup.id))
                .thenReturn(Optional.of(menuGroup))

            `when`(productRepository.findAllByIdIn(listOf(product.id)))
                .thenReturn(listOf(product))

            `when`(productRepository.findById(product.id))
                .thenReturn(Optional.of(product))

            `when`(purgomalumClient.containsProfanity(request.name))
                .thenReturn(false)

            //when & then
            val result = menuService.create(request)
            assertAll(
                { assertThat(result.id).isNotNull() },
                { assertThat(result.price).isEqualTo(request.price) },
                { assertThat(result.price).isEqualTo(request.price) }
            )
        }

        @Test
        fun `메뉴 생성 요청 - 가격이 없을 경우 실패`() {
            val menuGroup = MenuGroupSpec.of()
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)

            val request = Menu()
            request.name = "치킨"
            request.menuProducts = listOf(menuProduct)
            request.isDisplayed = true
            request.menuGroupId = menuGroup.id

            //when & then
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { menuService.create(request) }
        }

        @Test
        fun `메뉴 생성 요청 - 가격이 0보다 작을 경우 실패`() {
            val menuGroup = MenuGroupSpec.of()
            val product = ProductSpec.of(BigDecimal.valueOf(-1000))
            val menuProduct = MenuProductSpec.of(product, 2)

            val request = Menu()
            request.name = "치킨"
            request.menuProducts = listOf(menuProduct)
            request.isDisplayed = true
            request.menuGroupId = menuGroup.id

            //when & then
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { menuService.create(request) }
        }

        @Test
        fun `메뉴 생성 요청 - 메뉴의 이름이 없을 경우 실패`() {
            val menuGroup = MenuGroupSpec.of()
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)

            val request = Menu()
            request.price = BigDecimal.valueOf(9000)
            request.menuProducts = listOf(menuProduct)
            request.isDisplayed = true
            request.menuGroupId = menuGroup.id

            `when`(menuGroupRepository.findById(menuGroup.id))
                .thenReturn(Optional.of(menuGroup))

            `when`(productRepository.findAllByIdIn(listOf(product.id)))
                .thenReturn(listOf(product))

            `when`(productRepository.findById(product.id))
                .thenReturn(Optional.of(product))

            //when & then
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { menuService.create(request) }
        }

        @Test
        fun `메뉴 생성 요청 - 메뉴명에 욕설 및 비속어 포함 시 실패`() {
            val menuGroup = MenuGroupSpec.of()
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)

            val request = Menu()
            request.name = "치킨"
            request.price = BigDecimal.valueOf(9000)
            request.menuProducts = listOf(menuProduct)
            request.isDisplayed = true
            request.menuGroupId = menuGroup.id

            `when`(menuGroupRepository.findById(menuGroup.id))
                .thenReturn(Optional.of(menuGroup))

            `when`(productRepository.findAllByIdIn(listOf(product.id)))
                .thenReturn(listOf(product))

            `when`(productRepository.findById(product.id))
                .thenReturn(Optional.of(product))

            `when`(purgomalumClient.containsProfanity(request.name))
                .thenReturn(true)

            //when & then
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { menuService.create(request) }
        }

        @Test
        fun `메뉴 생성 요청 - 메뉴가 특정 메뉴그룹에 포함되지 않을 경우 실패`() {
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)

            val request = Menu()
            request.name = "치킨"
            request.price = BigDecimal.valueOf(9000)
            request.menuProducts = listOf(menuProduct)
            request.isDisplayed = true
            request.menuGroupId = UUID.randomUUID()

            `when`(menuGroupRepository.findById(request.menuGroupId))
                .thenReturn(Optional.empty())

            //when & then
            Assertions.assertThatExceptionOfType(NoSuchElementException::class.java)
                .isThrownBy { menuService.create(request) }
        }

        @Test
        fun `메뉴 생성 요청 - 메뉴의 메뉴상품이 없을 경우 실패`() {
            val menuGroup = MenuGroupSpec.of()

            val request = Menu()
            request.name = "치킨"
            request.price = BigDecimal.valueOf(9000)
            request.menuProducts = emptyList()
            request.isDisplayed = true
            request.menuGroupId = menuGroup.id

            `when`(menuGroupRepository.findById(menuGroup.id))
                .thenReturn(Optional.of(menuGroup))

            //when & then
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { menuService.create(request) }
        }

        @Test
        fun `메뉴 생성 요청 - 메뉴 가격이 (메뉴상품 가격 * 메뉴상품 재고 수)의 합보다 클 경우 실패`() {
            val menuGroup = MenuGroupSpec.of()
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)

            val request = Menu()
            request.name = "치킨"
            request.price = BigDecimal.valueOf(30000)
            request.menuProducts = listOf(menuProduct)
            request.isDisplayed = true
            request.menuGroupId = menuGroup.id

            `when`(menuGroupRepository.findById(menuGroup.id))
                .thenReturn(Optional.of(menuGroup))

            `when`(productRepository.findAllByIdIn(listOf(product.id)))
                .thenReturn(listOf(product))

            `when`(productRepository.findById(product.id))
                .thenReturn(Optional.of(product))

            //when & then
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { menuService.create(request) }
        }

        @Test
        fun `메뉴 생성 요청 - 메뉴상품의 재고량이 0보다 작을 경우 실패`() {
            val menuGroup = MenuGroupSpec.of()
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, -7)

            val request = Menu()
            request.name = "치킨"
            request.price = BigDecimal.valueOf(9000)
            request.menuProducts = listOf(menuProduct)
            request.isDisplayed = true
            request.menuGroupId = menuGroup.id

            `when`(menuGroupRepository.findById(menuGroup.id))
                .thenReturn(Optional.of(menuGroup))

            `when`(productRepository.findAllByIdIn(listOf(product.id)))
                .thenReturn(listOf(product))

            //when & then
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { menuService.create(request) }
        }

        @Test
        fun `메뉴 생성 요청 - 메뉴상품의 상품이 존재하지 않을 경우 실패`() {
            val menuGroup = MenuGroupSpec.of()
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)

            val request = Menu()
            request.name = "치킨"
            request.price = BigDecimal.valueOf(9000)
            request.menuProducts = listOf(menuProduct)
            request.isDisplayed = true
            request.menuGroupId = menuGroup.id

            `when`(menuGroupRepository.findById(menuGroup.id))
                .thenReturn(Optional.of(menuGroup))

            `when`(productRepository.findAllByIdIn(listOf(product.id)))
                .thenReturn(emptyList())

            //when & then
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { menuService.create(request) }
        }
    }

    @Nested
    @DisplayName("메뉴의 가격 변경")
    inner class `메뉴의 가격 변경` {
        @Test
        fun `정상적인 메뉴의 가격 변경 성공`() {
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)
            val menu = FakeMenuRepository.save(MenuSpec.of(listOf(menuProduct), BigDecimal.valueOf(8000)))

            val request = Menu()
            request.price = BigDecimal.valueOf(13000)

            //when
            val result = menuService.changePrice(menu.id, request)

            //then
            Assertions.assertThat(result.price).isEqualTo(BigDecimal.valueOf(13000))
        }

        @Test
        fun `메뉴 가격이 (메뉴상품 가격 * 메뉴상품 재고 수)의 합보다 클 경우 실패`() {
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)
            val menu = FakeMenuRepository.save(MenuSpec.of(listOf(menuProduct), BigDecimal.valueOf(8000)))

            val request = Menu()
            request.price = BigDecimal.valueOf(45000)

            //when & then
            Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { menuService.changePrice(menu.id, request) }
        }

    }

    @Nested
    @DisplayName("메뉴의 전시상태 활성화")
    inner class `메뉴의 전시상태 활성화` {
        @Test
        fun `정상적인 전시상태 활성화 성공`() {
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)
            val menu = FakeMenuRepository.save(MenuSpec.of(listOf(menuProduct), BigDecimal.valueOf(8000), false))

            //when
            val result = menuService.display(menu.id)

            //then
            Assertions.assertThat(result.isDisplayed).isTrue()
        }

        @Test
        fun `메뉴 가격이 (메뉴상품 가격 * 메뉴상품 재고 수)의 합보다 클 경우 실패`() {
            val product = ProductSpec.of(BigDecimal.valueOf(9000))
            val menuProduct = MenuProductSpec.of(product, 2)
            val menu = FakeMenuRepository.save(MenuSpec.of(listOf(menuProduct), BigDecimal.valueOf(35000), false))

            //when & then
            Assertions.assertThatExceptionOfType(IllegalStateException::class.java)
                .isThrownBy { menuService.display(menu.id) }
        }
    }

    @Test
    fun `상품의 가격이 변경될 경우 - 메뉴 가격이 (메뉴상품 가격 * 메뉴상품 재고 수)의 합보다 큰 메뉴는 전시 상태 종료됨`() {
        val requestPrice = BigDecimal.valueOf(5000)

        val product = ProductSpec.of(requestPrice)
        val menuProduct = MenuProductSpec.of(product, 2)
        val menu = FakeMenuRepository.save(MenuSpec.of(listOf(menuProduct), BigDecimal.valueOf(15000), true))

        `when`(productRepository.findById(product.id))
            .thenReturn(Optional.of(product))

        //when
        val request = Product()
        request.price = BigDecimal.valueOf(5000)

        productService.changePrice(product.id, request)

        assertThat(menu.isDisplayed).isFalse()
    }
}

