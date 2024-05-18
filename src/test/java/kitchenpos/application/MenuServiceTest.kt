package kitchenpos.application

import kitchenpos.domain.Menu
import kitchenpos.domain.MenuRepository
import kitchenpos.infra.PurgomalumClient
import kitchenpos.utils.generateUUIDFrom
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.util.NoSuchElementException

@SpringBootTest
@Sql("classpath:db/data.sql")
class MenuServiceTest {
    @MockBean
    private lateinit var purgomalumClient: PurgomalumClient

    @Autowired
    private lateinit var menuRepository: MenuRepository

    @Autowired
    private lateinit var sut: MenuService

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
            val notExistsMenuGroupUUID = "f1860abc2ea1411bbd4abaa44f0d1111"
            val request = Menu()
            request.price = BigDecimal.valueOf(16_000)
            request.menuGroupId = generateUUIDFrom(uuidWithoutDash = notExistsMenuGroupUUID)

            // when
            // then
            assertThrows<NoSuchElementException> { sut.create(request) }
        }
    }
}
