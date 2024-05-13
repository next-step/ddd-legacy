package kitchenpos.application

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kitchenpos.domain.FakeMenuGroupRepository
import kitchenpos.domain.MenuGroup
import org.junit.jupiter.api.assertThrows

private val menuGroupRepository = FakeMenuGroupRepository()
private val menuGroupService = MenuGroupService(menuGroupRepository)

class MenuGroupServiceMockTest : BehaviorSpec({
    given("메뉴 그룹을 생성할 때") {
        `when`("입력 값이 정상이면") {
            val newMenuGroup = MenuGroup().apply { name = "메뉴 그룹" }

            then("정상적으로 생성된다.") {
                with(menuGroupService.create(newMenuGroup)) {
                    this.id shouldNotBe null
                    this.name shouldBe "메뉴 그룹"
                }
            }
        }

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
            menuGroupRepository.clear()

            then("빈 목록을 반환한다.") {
                with(menuGroupService.findAll()) {
                    this.size shouldBe 0
                }
            }
        }
    }
})
