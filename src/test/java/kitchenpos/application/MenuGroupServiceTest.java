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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@UnitTest
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
        @DisplayName("실패: 이름 미지정 이면 MenuGroupNameException 발생")
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
            int expectedSize = 2;

            MenuGroup request1 = MenuGroupFixture.메뉴그룹_Request("추천 메뉴 그룹");
            MenuGroup request2 = MenuGroupFixture.메뉴그룹_Request("신 메뉴 그룹");

            menuGroupRepository.save(request1);
            menuGroupRepository.save(request2);

            // when
            List<MenuGroup> result = sut.findAll();

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result).hasSize(expectedSize),
                    () -> assertThat(result).extracting(MenuGroup::getName)
                            .containsExactlyInAnyOrder(request1.getName(), request2.getName())
            );
        }
    }
}
