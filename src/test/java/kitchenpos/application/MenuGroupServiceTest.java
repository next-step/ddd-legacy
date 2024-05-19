package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.testfixture.InMemoryMenuGroupRepository;
import kitchenpos.testfixture.MenuGroupTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;


class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Nested
    @DisplayName("메뉴그룹 생성")
    class create {

        @Test
        @DisplayName("메뉴그룹 생성 성공")
        void success() {
            //given
            MenuGroup request = MenuGroupTestFixture.createMenuGroupRequest("name");

            //when
            MenuGroup response = menuGroupService.create(request);

            //then
            assertEquals(request.getName(), response.getName());
        }

        @Test
        @DisplayName("메뉴그룹의 이름이 비어 있을수 없다.")
        void canNotEmptyName() {
            //given
            MenuGroup request = MenuGroupTestFixture.createMenuGroupRequest("");

            //when then
            assertThatThrownBy(() -> menuGroupService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("메뉴그룹 조회")
    class find{

        @Test
        @DisplayName("모든 메뉴그룹 조회")
        void findAll() {

            //given
            MenuGroup group1 = MenuGroupTestFixture.createMenuGroup("name1");
            MenuGroup group2 = MenuGroupTestFixture.createMenuGroup("name2");

            menuGroupRepository.save(group1);
            menuGroupRepository.save(group2);

            //when
            List<MenuGroup> response = menuGroupService.findAll();

            //then
            assertThat(response).hasSize(2);
            assertThat(response)
                    .filteredOn(MenuGroup::getId, group1.getId())
                    .containsExactly(group1);
            assertThat(response)
                    .filteredOn(MenuGroup::getId, group2.getId())
                    .containsExactly(group2);
        }
    }

}