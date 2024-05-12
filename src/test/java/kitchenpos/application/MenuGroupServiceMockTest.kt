package kitchenpos.application

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import org.junit.jupiter.api.assertThrows

private val menuGroupRepository = mockk<MenuGroupRepository>()
private val menuGroupService = MenuGroupService(menuGroupRepository)

class MenuGroupServiceMockTest : BehaviorSpec({
    given("메뉴 그룹을 생성할 때") {
        `when`("이름이 null 이면") {
            val newMenuGroup = MenuGroup().apply { name = null }

            then("예외가 발생한다.") {
                assertThrows<IllegalArgumentException> {
                    menuGroupService.create(newMenuGroup)
                }
            }
        }

        `when`("이름이 빈 문자열이면") {
            val newMenuGroup = MenuGroup().apply { name = "" }

            then("예외가 발생한다.") {
                assertThrows<IllegalArgumentException> {
                    menuGroupService.create(newMenuGroup)
                }
            }
        }
    }

    given("메뉴 그룹을 조회할 떄") {
        `when`("메뉴 그룹이 존재하지 않으면") {
            every { menuGroupRepository.findAll() } returns mutableListOf()

            then("빈 목록을 반환한다.") {
                val results = menuGroupService.findAll()
                results.size shouldBe 0
            }
        }
    }
})
