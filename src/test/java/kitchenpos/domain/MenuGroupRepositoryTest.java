package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupRepositoryTest {

    private FakeMenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();

    protected static final UUID FIRST_ID = UUID.randomUUID();
    protected static final UUID SECOND_ID = UUID.randomUUID();

    private MenuGroup createMenuGroup(UUID id, String menuGroupName) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(menuGroupName);
        return menuGroup;
    }

    @DisplayName("메뉴그룹 저장")
    @Test
    void save() {
        MenuGroup menuGroup = createMenuGroup(FIRST_ID, "일식");

        MenuGroup saveMenuGroup = menuGroupRepository.save(menuGroup);

        assertThat(menuGroup.getId()).isEqualTo(saveMenuGroup.getId());
    }

    @DisplayName("메뉴그룹 내역 조회")
    @Test
    void findAll() {
        // given
        MenuGroup menuGroup = createMenuGroup(FIRST_ID, "한식");
        MenuGroup menuGroup2 = createMenuGroup(SECOND_ID, "중식");

        menuGroupRepository.save(menuGroup);
        menuGroupRepository.save(menuGroup2);

        // then
        List<MenuGroup> findAll = menuGroupRepository.findAll();

        // when
        assertThat(findAll.size()).isEqualTo(2);
    }
}