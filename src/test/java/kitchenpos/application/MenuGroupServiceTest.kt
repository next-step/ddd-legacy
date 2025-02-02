package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kitchenpos.domain.MenuGroupRepository
import kitchenpos.fixture.MenuGroupFixture

class MenuGroupServiceTest : BehaviorSpec({

    val menuGroupRepository = mockk<MenuGroupRepository>()
    val sut = MenuGroupService(menuGroupRepository)

    given("create 메서드가 호출되었을 때") {
        `when`("유효한 이름이 제공되면") {
            beforeEach {
                clearAllMocks()
                val request = MenuGroupFixture.create(name = "Valid Name")
                every { menuGroupRepository.save(any()) } returns request
            }

            then("MenuGroup이 정상적으로 생성되고 저장되어야 한다") {
                val request = MenuGroupFixture.create(name = "Valid Name")
                val result = sut.create(request)

                result.name shouldBe request.name

                verify(exactly = 1) { menuGroupRepository.save(any()) }
            }
        }

        `when`("이름이 null이거나 비어있으면") {
            beforeEach {
                clearAllMocks()
            }

            then("IllegalArgumentException이 발생해야 한다") {
                val requestWithNullName = MenuGroupFixture.create(name = null)
                val requestWithEmptyName = MenuGroupFixture.create(name = "")

                shouldThrow<IllegalArgumentException> {
                    sut.create(requestWithNullName)
                }

                shouldThrow<IllegalArgumentException> {
                    sut.create(requestWithEmptyName)
                }

                verify(exactly = 0) { menuGroupRepository.save(any()) }
            }
        }
    }
})
