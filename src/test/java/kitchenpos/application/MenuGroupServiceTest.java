package kitchenpos.application;

import kitchenpos.IntegrationTestSupport;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class MenuGroupServiceTest extends IntegrationTestSupport {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;


    @AfterEach
    void tearDown() {
        menuGroupRepository.deleteAllInBatch();
    }

    @DisplayName("메뉴 그룹을 등록할 수 있다.")
    @Test
    void createMenuGroup() {
        // given
        final MenuGroup expected = createMenuGroupRequest("치킨");
        final MenuGroup actual = menuGroupService.create(expected);

        // when & then
        assertThat(actual.getId()).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName())
        );
    }

    @DisplayName("메뉴 그룹의 이름이 존재하지 않으면 등록할 수 없다")
    @NullAndEmptySource
    @ParameterizedTest
    void createMenuGroup_WithoutName_ShouldThrowException(final String name) {
        // given & when & then
        final MenuGroup expected = createMenuGroupRequest(name);
        assertThatThrownBy(() -> menuGroupService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 그룹의 이름이 존재해야 합니다.");
    }

    @DisplayName("메뉴 그룹의 목록을 조회할 수 있다.")
    @Test
    void findAllMenuGroup() {
        // given
        final MenuGroup expected1 = createMenuGroupRequest("치킨");
        final MenuGroup expected2 = createMenuGroupRequest("피자");

        // when
        menuGroupService.create(expected1);
        menuGroupService.create(expected2);
        final List<MenuGroup> response = menuGroupService.findAll();

        // then
        assertThat(response).hasSize(2);
     }

    private MenuGroup createMenuGroupRequest(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }
}
