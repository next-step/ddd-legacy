package kitchenpos.application

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuProduct
import kitchenpos.domain.ProductRepository
import kitchenpos.infra.PurgomalumClient
import kitchenpos.utils.generateUUIDFrom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.anyString
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@Sql("classpath:db/data.sql")
class MenuServiceTest {
    @MockBean
    private lateinit var purgomalumClient: PurgomalumClient

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var sut: MenuService

    companion object {
        private val NOT_EXISTING_MENU_GROUP_ID = generateUUIDFrom("f1860abc2ea1411bbd4abaa44f0d1111")
        private val EXISTING_MENU_GROUP_ID = generateUUIDFrom("f1860abc2ea1411bbd4abaa44f0d5580")
        private val EXISTING_MENU_ID = generateUUIDFrom("b9c670b04ef5409083496868df1c7d62")
        private val EXISTING_PRODUCT_ID_1 = generateUUIDFrom("3b52824434f7406bbb7e690912f66b10")
        private val EXISTING_PRODUCT_ID_2 = generateUUIDFrom("c5ee925c3dbb4941b825021446f24446")
    }

    @DisplayName("등록된 상품 목록을 조회할 수 있다.")
    @Test
    fun find_all_menus_test() {
        // when
        val products = sut.findAll()

        // then
        assertThat(products.size).isEqualTo(6)
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Nested
    inner class CreateMenu {
        @DisplayName("메뉴의 가격은 `0원` 보다 작을 수 없다.")
        @Test
        fun case_1() {
            // given
            val request = Menu()
            request.price = BigDecimal.valueOf(-1000)

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("메뉴는 이미 등록된 `메뉴 그룹` 에 속한다. (w/ 등록되지 않은 메뉴그룹)")
        @Test
        fun case_2() {
            // given
            val request = Menu()
            request.price = BigDecimal.valueOf(16_000)
            request.menuGroupId = NOT_EXISTING_MENU_GROUP_ID

            // when
            // then
            assertThrows<NoSuchElementException> { sut.create(request) }
        }

        @DisplayName("메뉴는 1개 이상의 `메뉴 구성 상품` 을 가지고 있어야 한다. ")
        @Test
        fun case_3() {
            // given
            val request = Menu()
            request.price = BigDecimal.valueOf(16_000)
            request.menuGroupId = EXISTING_MENU_GROUP_ID
            request.menuProducts = null

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("메뉴의 `메뉴 구성 상품` 은 이미 등록된 `상품` 으로만 등록 가능하다. (w/ 존재하지 않는 상품으로 등록)")
        @Test
        fun case_4() {
            // given
            val request = Menu()
            request.price = BigDecimal.valueOf(16_000)
            request.menuGroupId = EXISTING_MENU_GROUP_ID
            request.menuProducts = null

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("메뉴의 `메뉴 구성 상품` 은 이미 등록된 `상품` 으로만 등록 가능하다. (w/ 존재하는 상품으로 등록)")
        @Test
        fun case_5() {
            // given
            val menuProduct = generateMenuProduct(productId = EXISTING_PRODUCT_ID_1)

            val request = Menu()
            request.price = BigDecimal.valueOf(16_000)
            request.menuGroupId = EXISTING_MENU_GROUP_ID
            request.menuProducts = listOf(menuProduct)
            request.name = "후라이드 치킨"

            // when
            val createdMenu = sut.create(request)

            // then
            assertThat(createdMenu.id).isNotNull()
        }

        @DisplayName("메뉴 구성 상품의 개수와 상품의 개수는 같아야 한다. (한개는 등록된 상품 / 한개는 등록되지 않은 상품)")
        @Test
        fun case_6() {
            // given
            val menuProducts =
                listOf(
                    generateMenuProduct(productId = EXISTING_PRODUCT_ID_1),
                    generateMenuProduct(productId = UUID.randomUUID()),
                )

            val request = Menu()
            request.price = BigDecimal.valueOf(16_000)
            request.menuGroupId = EXISTING_MENU_GROUP_ID
            request.menuProducts = menuProducts
            request.name = "후라이드 치킨"

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("메뉴 구성 상품의 개수와 상품의 개수는 같아야 한다. (두개 모두 존재하는 상품)")
        @Test
        fun case_7() {
            // given
            val menuProducts =
                listOf(
                    generateMenuProduct(productId = EXISTING_PRODUCT_ID_1),
                    generateMenuProduct(productId = EXISTING_PRODUCT_ID_2),
                )

            val request = Menu()
            request.price = BigDecimal.valueOf(16_000)
            request.menuGroupId = EXISTING_MENU_GROUP_ID
            request.menuProducts = menuProducts
            request.name = "후라이드 치킨"

            // when
            val createdMenu = sut.create(request)

            // then
            assertThat(createdMenu.id).isNotNull()
        }

        @DisplayName("메뉴 구성 상품의 수량은 0 보다 작을수 없다.")
        @Test
        fun case_8() {
            // given
            val menuProducts =
                listOf(generateMenuProduct(productId = EXISTING_PRODUCT_ID_1, quantity = -1))

            val request = Menu()
            request.price = BigDecimal.valueOf(16_000)
            request.menuGroupId = EXISTING_MENU_GROUP_ID
            request.menuProducts = menuProducts
            request.name = "후라이드 치킨"

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("메뉴의 가격은 메뉴 구성 상품 가격의 총합보다 클 수 없다.")
        @Test
        fun case_9() {
            // given
            val menuProducts = listOf(generateMenuProduct(productId = EXISTING_PRODUCT_ID_1))

            val request = Menu()
            request.price = BigDecimal.valueOf(17_000)
            request.menuGroupId = EXISTING_MENU_GROUP_ID
            request.menuProducts = menuProducts
            request.name = "후라이드 치킨"

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("메뉴 이름에는 비속어가 들어갈 수 없다.")
        @Test
        fun case_10() {
            // given
            given(purgomalumClient.containsProfanity(anyString())).willReturn(true)
            val menuProducts = listOf(generateMenuProduct(productId = EXISTING_PRODUCT_ID_1))

            val request = Menu()
            request.price = BigDecimal.valueOf(17_000)
            request.menuGroupId = EXISTING_MENU_GROUP_ID
            request.menuProducts = menuProducts
            request.name = "비속어 치킨"

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }

        @DisplayName("메뉴는 이름, 가격, 메뉴 구성 상품 정보를 꼭 입력해야 한다.")
        @Test
        fun case_11() {
            // given
            given(purgomalumClient.containsProfanity(anyString())).willReturn(false)
            val menuProducts = listOf(generateMenuProduct(productId = EXISTING_PRODUCT_ID_1))

            val request = Menu()
            request.price = BigDecimal.valueOf(17_000)
            request.menuGroupId = EXISTING_MENU_GROUP_ID
            request.menuProducts = menuProducts

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }
    }

    @DisplayName("메뉴는 노출상태를 변경 가능하다.")
    @Nested
    inner class ChangeDisplayOption {
        @DisplayName("메뉴를 숨길 수 있다.")
        @Test
        fun case_12() {
            // given

            // when
            val hiddenMenu = sut.hide(EXISTING_MENU_ID)

            // then
            assertThat(hiddenMenu.isDisplayed).isFalse()
        }

        @DisplayName("메뉴를 노출할 수 있다.")
        @Test
        fun case_13() {
            // given

            // when
            val displayedMenu = sut.display(EXISTING_MENU_ID)

            // then
            assertThat(displayedMenu.isDisplayed).isTrue()
        }

        @DisplayName("메뉴의 가격이 `메뉴 구성 상품` 가격 총합보다 크면 노출할 수 없다")
        @Test
        fun case_14() {
            // given
            val product = productRepository.findById(generateUUIDFrom("0ac16db71b024a87b9c1e7d8f226c48d")).get()
            product.price -= BigDecimal.valueOf(4000)
            productRepository.saveAndFlush(product)

            // when
            // then
            assertThrows<IllegalStateException> { sut.display(EXISTING_MENU_ID) }
        }
    }

    private fun generateMenuProduct(
        productId: UUID,
        quantity: Long = 1L,
    ): MenuProduct {
        val menuProduct = MenuProduct()
        menuProduct.productId = productId
        menuProduct.quantity = quantity
        return menuProduct
    }
}
