package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MenuGroupServiceTest {

    private final String menuGroupName = "두마리세트";

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private SoftAssertions softAssertions;

    @BeforeEach
    void initSoftAssertions() {
        softAssertions = new SoftAssertions();
    }

    @AfterEach
    void checkAssertions() {
        softAssertions.assertAll();
    }

    @DisplayName("메뉴그룹 생성")
    @Test
    void create() {
        MenuGroup menuGroupRequest = new MenuGroup(menuGroupName);
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);
        assertThat(menuGroup).isNotNull();
    }

    @DisplayName("메뉴그룹 생성")
    @Test
    void createValidation() {
        MenuGroup nameNullRequest = new MenuGroup(null);
        assertThatThrownBy(() -> menuGroupService.create(nameNullRequest))
                .isInstanceOf(IllegalArgumentException.class);

        MenuGroup emptyNameRequest = new MenuGroup("");
        assertThatThrownBy(() -> menuGroupService.create(emptyNameRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("모든 메뉴그룹 조회")
    @Test
    void findAll() {
        MenuGroup menuGroupRequest = new MenuGroup(menuGroupName);
        menuGroupService.create(menuGroupRequest);

        List<MenuGroup> menuGroups = menuGroupRepository.findAll();
        assertThat(menuGroups.size()).isEqualTo(menuGroupService.findAll().size());
    }
}