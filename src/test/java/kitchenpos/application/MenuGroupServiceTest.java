package kitchenpos.application;

import config.UnitTest;
import kitchenpos.MenuGroupFixture;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.infra.InmemoryMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("메뉴 그룹 서비스 테스트")
class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository;

    private MenuGroupService sut;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InmemoryMenuGroupRepository();
        sut = new MenuGroupService(menuGroupRepository);
    }

    @Nested
    @DisplayName("메뉴 그룹 생성")
    class CreateMenuGroup {
        @Test
        @DisplayName("성공: 유효한 이름")
        void createMenuGroup() {
            // given
            MenuGroup request = MenuGroupFixture.추천_메뉴그룹_Request();
            String expectedName = request.getName();

            // when
            MenuGroup saved = sut.create(request);

            // then
            assertAll(
                    () -> assertThat(saved).isNotNull(),
                    () -> assertThat(saved.getId()).isNotNull(),
                    () -> assertThat(saved.getName()).isEqualTo(expectedName)
            );
        }

        @Test
        @DisplayName("실패: 이름 미지정 이면 IllegalArgumentException 발생")
        void createMenuGroupWithoutNameThrowsIllegalArgumentException() {
            // given
            MenuGroup request = MenuGroupFixture.메뉴그룹_Request(null);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(request));
        }
    }


    @Nested
    @DisplayName("메뉴 그룹 목록 조회")
    class FindAllMenuGroups {
        @Test
        @DisplayName("성공: 메뉴 그룹이 존재하면 모두 조회")
        void findAllMenuGroups() {
            // given
            List<MenuGroup> menuGroups = List.of(
                    MenuGroupFixture.추천_메뉴그룹_Request(),
                    MenuGroupFixture.신_메뉴그룹()
            );

            int expectedSize = menuGroups.size();

            // when
            List<MenuGroup> result = sut.findAll();

            // then
            assertThat(result).hasSize(expectedSize);
            verify(menuGroupRepository).findAll();
        }
    }
}
