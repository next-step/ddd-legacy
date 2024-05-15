package kitchenpos.application

import domain.MenuGroupFixtures.makeMenuGroupOne
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kitchenpos.application.fake.FakeMenuGroupRepository
import kitchenpos.domain.MenuGroup
import org.junit.jupiter.api.assertThrows
import java.util.*

class MenuGroupServiceTest : DescribeSpec() {
    init {
        describe("MenuGroupService 클래스의") {
            val menuGroupService = MenuGroupService(FakeMenuGroupRepository())
            describe("create 메서드는") {
                context("정상적인 메뉴 그룹이 주어졌을 때") {
                    val createMenuGroupRequest = makeMenuGroupOne()
                    it("메뉴 그룹을 생성한다") {
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
