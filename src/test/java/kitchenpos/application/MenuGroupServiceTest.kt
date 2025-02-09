package kitchenpos.application

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kitchenpos.domain.MenuGroupRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import random.randomMenuGroup

class MenuGroupServiceTest {
    private val menuGroupRepository: MenuGroupRepository = mockk(relaxed = true)
    private lateinit var menuGroupService: MenuGroupService

    @BeforeEach
    fun setUp() {
        menuGroupService = MenuGroupService(menuGroupRepository)
    }

    @ParameterizedTest
    @NullAndEmptySource
    fun `메뉴 그룹명이 없을 경우 예외 발생`(input: String?) {
        val menuGroup = randomMenuGroup(name = input)
        assertThrows<IllegalArgumentException> {
            menuGroupService.create(menuGroup)
        }
    }

    @Test
    fun `메뉴 그룹 생성`() {
        // given
        val menuGroup = randomMenuGroup()
        every { menuGroupRepository.save(any()) } returns menuGroup

        // when
        menuGroupService.create(menuGroup)

        // then
        verify { menuGroupRepository.save(any()) }
    }

    @Test
    fun `메뉴 그룹 조회`() {
        // given
        val expectedMenuGroups = listOf(randomMenuGroup(), randomMenuGroup())
        every { menuGroupRepository.findAll() } returns expectedMenuGroups

        // when
        val result = menuGroupService.findAll()

        // then
        result shouldBe expectedMenuGroups
    }
}
