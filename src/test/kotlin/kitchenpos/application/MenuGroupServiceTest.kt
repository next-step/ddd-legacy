package kitchenpos.application

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import org.junit.jupiter.api.assertThrows
import java.util.*

class MenuGroupServiceTest : DescribeSpec() {
    init {
        describe("MenuGroupService 클래스의") {
            val menuGroupRepository = mockk<MenuGroupRepository>()
            val menuGroupService = MenuGroupService(menuGroupRepository)
            describe("create 메서드는") {
                context("정상적인 메뉴 그룹이 주어졌을 때") {
                    val createMenuGroupRequest =
                        MenuGroup().apply {
                            this.id = UUID.randomUUID()
                            this.name = "테스트 메뉴 그룹"
                        }
                    it("메뉴 그룹을 생성한다") {
                        every { menuGroupRepository.save(any<MenuGroup>()) } returns createMenuGroupRequest
                        val result = menuGroupService.create(createMenuGroupRequest)

                        result.name shouldBe createMenuGroupRequest.name
                    }
                }

                context("name이 비어있거나 null인 메뉴 그룹이 주어졌을 때") {
                    val emptyNameRequest =
                        MenuGroup().apply {
                            this.id = UUID.randomUUID()
                            this.name = ""
                        }

                    val nullNameRequest =
                        MenuGroup().apply {
                            this.id = UUID.randomUUID()
                            this.name = null
                        }
                    it("IllegalArgumentException을 던진다") {

                        assertSoftly {
                            assertThrows<IllegalArgumentException> {
                                menuGroupService.create(emptyNameRequest)
                            }
                            assertThrows<IllegalArgumentException> {
                                menuGroupService.create(nullNameRequest)
                            }
                        }
                    }
                }
            }
        }
    }
}
