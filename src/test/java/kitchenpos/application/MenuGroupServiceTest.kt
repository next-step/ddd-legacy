package kitchenpos.application

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import java.util.*

@ExtendWith(MockKExtension::class)
internal class MenuGroupServiceTest {
    @MockK
    private lateinit var menuGroupRepository: MenuGroupRepository

    @InjectMockKs
    private lateinit var menuGroupService: MenuGroupService

    @Nested
    inner class `메뉴 그룹 생성 테스트` {
        @DisplayName("메뉴 그룹에 이름이 null 또는 길이가 0일 경우, IllegalArgumentException 예외 처리를 한다.")
        @ParameterizedTest
        @NullAndEmptySource
        fun test1(name: String?) {
            // given
            val request = MenuGroup().apply {
                this.id = UUID.randomUUID()
                this.name = name
            }

            // when & then
            shouldThrowExactly<IllegalArgumentException> {
                menuGroupService.create(request)
            }
        }

        @DisplayName("메뉴 그룹이 정상적일경우, 메뉴 그룹이 생성된다.")
        @Test
        fun test2() {
            // given
            val request = MenuGroup().apply {
                this.id = UUID.randomUUID()
                this.name = "테스트 그룹"
            }

            every { menuGroupRepository.save(any()) } returns request

            // when
            val result = menuGroupService.create(request)

            // then
            result.id shouldBe request.id
            result.name shouldBe request.name
        }
    }
}