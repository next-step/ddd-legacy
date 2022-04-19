package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.exception.NameNotEmptyException;
import kitchenpos.repository.InMemoryMenuGroupRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <pre>
 * kitchenpos.application
 *      MenuGroupServiceTest
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-22 오후 9:20
 */

@DisplayName("[메뉴 그룹]")
@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        this.menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Test
    @DisplayName("메뉴 그룹 이름값을 필수로 갖는다")
    public void requiredMenuGroupName() {
        MenuGroup menuGroup = new MenuGroup();
        AssertionsForClassTypes.assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(NameNotEmptyException.class);
    }

    @Test
    @DisplayName("메뉴 그룹을 등록한다")
    public void createMenuGroup() {
        MenuGroup menuGroup = createMenuGroupRequest("세트 메뉴");

        final MenuGroup actual = menuGroupService.create(menuGroup);

        Assertions.assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(menuGroup.getName())
        );
    }

    @Test
    @DisplayName("메뉴 그룹을 전체 조회할 수 있다.")
    public void searchMenuGroupAll() {

        menuGroupRepository.save(createMenuGroup("한마리 메뉴"));
        menuGroupRepository.save(createMenuGroup("신메뉴"));
        menuGroupRepository.save(createMenuGroup("두마리 메뉴"));

        final List<MenuGroup> actual = menuGroupService.findAll();

        assertThat(actual).hasSize(3);
    }

    private MenuGroup createMenuGroupRequest(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        return menuGroup;
    }

    private MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);

        return menuGroup;
    }
}
