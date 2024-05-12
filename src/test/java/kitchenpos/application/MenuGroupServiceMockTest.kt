package kitchenpos.application

import kitchenpos.domain.MenuGroup
import kitchenpos.domain.MenuGroupRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import utils.BaseMockTest

@ExtendWith(MockitoExtension::class)
class MenuGroupServiceMockTest : BaseMockTest() {

    @Mock
    private lateinit var menuGroupRepository: MenuGroupRepository

    @InjectMocks
    private lateinit var menuGroupService: MenuGroupService

    @Test
    fun `메뉴 그룹 생성 - 정상적인 메뉴 그룹 생성 성공`() {
        val request = MenuGroup()
        request.name = "test menu group"

        Assertions.assertThatCode { menuGroupService.create(request) }
            .doesNotThrowAnyException()
    }

    @Test
    fun `메뉴 그룹 생성 - 이름이 공백인 메뉴 그룹 생성 시 실패`() {
        val request = MenuGroup()
        request.name = "     "

        Assertions.assertThatCode { menuGroupService.create(request) }
            .doesNotThrowAnyException()
    }
}
