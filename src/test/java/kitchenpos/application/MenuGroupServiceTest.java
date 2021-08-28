package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private MenuGroup menuGroup;

    @BeforeEach
    void setup() {
        menuGroup = new MenuGroup();
        menuGroup.setName("메뉴그룹1");
        menuGroup.setId(UUID.randomUUID());
    }

    @DisplayName("메뉴그룹명이 빈값이거나 null이면 등록할 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void emptyMenuGroupNameTest(String menuGroupName) {
        // given
        menuGroup.setName(menuGroupName);

        // when
        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                // then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴그룹을 등록한다.")
    @Test
    void createTest() {
        // given
        // when
        MenuGroup actual = menuGroupService.create(menuGroup);
        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("메뉴그룹1");
    }

    @DisplayName("메뉴그룹 목록을 조회한다.")
    @Test
    void findAllTest() {
        // given
        // when
        List<MenuGroup> menuGroups = menuGroupService.findAll();
        // then
        assertThat(menuGroups.size()).isEqualTo(12);
    }
}