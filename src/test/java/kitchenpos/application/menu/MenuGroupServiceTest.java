package kitchenpos.application.menu;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Application: 메뉴 그룹 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    private MenuGroup menuGroup;

    @BeforeEach
    void setup() {
        menuGroup = MenuTestFixture.aMenuGroup();
    }

    @Nested
    @DisplayName("등록이 실패하는 경우")
    class create_fail_cases {

        @DisplayName("메뉴 그룹을 생성 할 때 이름이 비어있으면 예외가 발생한다.")
        @Test
        void fall_test_case_1() {
            // given
            ReflectionTestUtils.setField(menuGroup, "name", null);
            // when
            // then
            assertThrows(IllegalArgumentException.class, () -> menuGroupService.create(menuGroup));
        }
    }

    @Nested
    @DisplayName("등록이 성공하는 경우")
    class create_success_test_cases {

        @DisplayName("메뉴 그룹을 생성 할 수 있다.")
        @Test
        void case_1() {
            // given
            MenuGroup response = new MenuGroup();
            response.setId(menuGroup.getId());
            response.setName(menuGroup.getName());

            // when
            when(menuGroupRepository.save(any(MenuGroup.class))).thenReturn(menuGroup);
            MenuGroup saved = menuGroupService.create(response);

            // then
            assertEquals(menuGroup.getId(), saved.getId());
            assertEquals(menuGroup.getName(), saved.getName());
        }

    }

    @Nested
    @DisplayName("조회가 성공하는 경우")
    class search_success_test_cases {

        @DisplayName("메뉴 그룹을 조회 할 수 있다.")
        @Test
        void case_1() {
            // given
            // when
            when(menuGroupRepository.findAll()).thenReturn(List.of(menuGroup));
            // then
            assertEquals(menuGroupService.findAll().size(), 1);
        }

        @DisplayName("조회된 내역이 없는 경우 빈 리스트가 반환된다.")
        @Test
        void case_2() {
            // given
            // when
            when(menuGroupRepository.findAll()).thenReturn(List.of());
            // then
            assertEquals(menuGroupService.findAll().size(), 0);
        }
    }
}
