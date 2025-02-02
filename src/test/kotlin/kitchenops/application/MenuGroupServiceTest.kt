package kitchenops.application

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kitchenpos.application.MenuGroupService
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import spec.BaseUnitSpec
import java.util.*

internal class MenuGroupServiceTest : BaseUnitSpec({

    val menuGroupRepository = mockk<MenuGroupRepository>()
    val sut = MenuGroupService(menuGroupRepository)

    context("메뉴 그룹을 생성할 수 있다") {
        test("이름을 지정하지 않으면 생성할 수 없다") {
            // given
            val request = MenuGroup().apply { name = null }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("이름이 비어있으면 생성할 수 없다") {
            // given
            val request = MenuGroup().apply { name = "" }

            // when
            val actual = runCatching { sut.create(request) }

            // then
            actual.exceptionOrNull() shouldBe IllegalArgumentException()
        }

        test("생성한다") {
            // given
            val requestName = "myName"
            val request = MenuGroup().apply { name = requestName }

            every { menuGroupRepository.save(match { it.name == requestName }) } returns request

            // when
            val actual = sut.create(request)

            // then
            actual.name shouldBe "myName"

            verify(exactly = 1) { menuGroupRepository.save(any()) }
        }
    }

    context("메뉴 그룹을 전체 조회할 수 있다") {
        test("전체 조회한다") {
            // given
            val menuGroup1 = MenuGroup().apply { id = UUID.randomUUID() }
            val menuGroup2 = MenuGroup().apply { id = UUID.randomUUID() }

            every { menuGroupRepository.findAll() } returns listOf(menuGroup1, menuGroup2)

            // when
            val actual = sut.findAll()

            // then
            actual shouldBe listOf(menuGroup1, menuGroup2)
        }
    }
})
