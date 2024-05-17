package kitchenpos.application

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.testsupport.FakeMenuGroupRepository
import kitchenpos.testsupport.MenuGroupFixtures.createMenuGroup

class MenuGroupFindAllServiceTest : ShouldSpec({
    lateinit var menuGroupRepository: MenuGroupRepository
    lateinit var service: MenuGroupService

    beforeTest {
        menuGroupRepository = FakeMenuGroupRepository()

        service = MenuGroupService(
            menuGroupRepository
        )
    }

    context("메뉴 그룹 목록 조회") {
        should("성공") {
            // given
            val savedMenuGroup = menuGroupRepository.save(
                createMenuGroup()
            )

            // when
            val result = service.findAll()

            // then
            result.shouldNotBeEmpty()
            result.first() should { menuGroup ->
                menuGroup.id shouldBe savedMenuGroup.id
                menuGroup.name shouldBe savedMenuGroup.name
            }
        }
    }
})
