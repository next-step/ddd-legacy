package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @InjectMocks
    private MenuGroupService menuGroupService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Test
    @DisplayName("메뉴 그룹 추가 - 성공")
    void addMenuGroup() {
        // given
        MenuGroup mockMenuGroup = generateMenuGroup(UUID.randomUUID());

        // mocking
        given(menuGroupRepository.save(any())).willReturn(mockMenuGroup);

        // when
        MenuGroup newMenuGroup = menuGroupService.create(mockMenuGroup);

        // then
        assertThat(newMenuGroup.getId()).isNotNull();
        assertThat(newMenuGroup.getName()).isEqualTo(mockMenuGroup.getName());
    }

    private MenuGroup generateMenuGroup(UUID id) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName("menu group 1");
        return menuGroup;
    }

    @Test
    @DisplayName("메뉴 그룹 추가 - 실패: 빈 입력 값")
    void addMenuGroup_BadRequest_Empty_Input() {
        // given
        MenuGroup menuGroup = generateMenuGroup(UUID.randomUUID());

        // when
        menuGroup.setName(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> menuGroupService.create(menuGroup));
    }

    @Test
    @DisplayName("모든 메뉴 그룹 조회")
    void findAllMenuGroup() {
        // given
        int size = 10;
        List<MenuGroup> mockMenuGroups = generateMenuGroups(size);

        // mocking
        given(menuGroupRepository.findAll()).willReturn(mockMenuGroups);

        // when
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        // then
        assertThat(menuGroups.size()).isEqualTo(size);
    }

    private List<MenuGroup> generateMenuGroups(int size) {
        return IntStream.range(0, size).mapToObj(i -> generateMenuGroup(UUID.randomUUID())).collect(Collectors.toList());
    }
}