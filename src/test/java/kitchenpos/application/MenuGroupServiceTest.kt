package kitchenpos.application

import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class MenuGroupServiceTest(
    private val sut: MenuGroupService,
    private val menuGroupRepository: MenuGroupRepository,
) {
    @DisplayName("메뉴 그룹 등록이 가능하다.")
    @Nested
    inner class Create {
        @DisplayName("메뉴그룹 생성시 'name' 은 필수다 - ('name' 있을시)")
        @Test
        fun create_menu_group_test1() {
            // given
            val request = MenuGroup()
            request.name = "한마리 치킨"

            // when
            val createdMenuGroup = sut.create(request)

            // then
            assertThat(createdMenuGroup.id).isNotNull()
        }

        @DisplayName("메뉴그룹 생성시 'name' 은 필수다 - ('name' 없을때)")
        @Test
        fun create_menu_group_test2() {
            // given
            val request = MenuGroup()

            // when
            // then
            assertThrows<IllegalArgumentException> { sut.create(request) }
        }
    }

    @DisplayName("등록된 메뉴그룹 목록을 조회할 수 있다")
    @Test
    fun find_all_menu_group_test() {
        // given
        // when
        val menuGroups = sut.findAll()

        // then
        assertThat(menuGroups.size).isGreaterThan(0)
    }
}
