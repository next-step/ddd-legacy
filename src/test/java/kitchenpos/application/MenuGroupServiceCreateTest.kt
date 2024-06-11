package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import java.util.UUID
import kitchenpos.domain.MenuGroup
import kitchenpos.testsupport.FakeMenuGroupRepository

class MenuGroupServiceCreateTest : ShouldSpec({
    lateinit var service: MenuGroupService

    beforeTest {
        service = MenuGroupService(
            FakeMenuGroupRepository()
        )
    }

    context("메뉴 그룹 생성") {
        should("성공") {
            // given
            val request = createRequest(
                id = null
            )

            // when
            val result = service.create(request)

            // then
            result.name shouldBe request.name
        }

        should("실패 - 메뉴 그룹명이 입력되지 않은 경우") {
            // given
            val request = createRequest(
                id = null,
                name = null
            )

            // when
            val result = shouldThrowAny {
                service.create(request)
            }

            // then
            result.shouldBeTypeOf<IllegalArgumentException>()
        }

        should("실패 - 메뉴 그룹명이 비어있는 경우") {
            // given
            val request = createRequest(
                id = null,
                name = ""
            )

            // when
            val result = shouldThrowAny {
                service.create(request)
            }

            // then
            result.shouldBeTypeOf<IllegalArgumentException>()
        }
    }
}) {
    companion object {
        private fun createRequest(
            id: UUID? = UUID.randomUUID(),
            name: String? = "test-menu-group-name"
        ): MenuGroup {
            return MenuGroup().apply {
                this.id = id
                this.name = name

            }
        }
    }
}
