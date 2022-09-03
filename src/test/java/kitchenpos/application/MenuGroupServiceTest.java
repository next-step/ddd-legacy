package kitchenpos.application;

import static kitchenpos.domain.MenuGroupFixture.MenuGroup;
import static kitchenpos.domain.MenuGroupFixture.MenuGroupWithUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("새로운 메뉴그룹을 생성할 수 있다.")
    @Nested
    class Create {

        @DisplayName("성공")
        @Test
        void create() {
            // given
            MenuGroup request = MenuGroup("한마리");

            // when
            MenuGroup savedMenuGroup = menuGroupService.create(request);

            // then
            assertThat(savedMenuGroup.getId()).isNotNull();
            assertThat(savedMenuGroup.getName()).isEqualTo("한마리");
        }

        @DisplayName("메뉴 이름은 공백일 수 없다.")
        @Test
        void nameBlankException() {
            // given
            MenuGroup request = MenuGroup("");

            // when, then
            assertThatThrownBy(() -> menuGroupService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴 이름은 null 일 수 없다.")
        @Test
        void nameNullException() {
            // given
            MenuGroup request = MenuGroup(null);

            // when, then
            assertThatThrownBy(() -> menuGroupService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("전체 메뉴그룹을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        MenuGroup 한마리 = menuGroupRepository.save(MenuGroupWithUUID("한마리"));
        MenuGroup 두마리 = menuGroupRepository.save(MenuGroupWithUUID("두마리"));

        // when
        List<MenuGroup> results = menuGroupService.findAll();

        // then
        assertThat(results).usingRecursiveFieldByFieldElementComparator()
            .containsExactly(한마리, 두마리);
    }
}
