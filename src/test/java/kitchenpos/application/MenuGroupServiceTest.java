package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @InjectMocks
    MenuGroupService service;
    @Mock
    MenuGroupRepository repository;

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름이 null 이거나 비어있다면 예외가 발생한다")
    void createNull(final String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.create(menuGroup));
    }

    @ParameterizedTest
    @ValueSource(strings = {"메뉴그룹1", "메뉴그룹2", "메뉴그룹3"})
    @DisplayName("이름이 있다면 메뉴그룹 생성가능")
    void createNotNull(final String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        when(repository.save(any(MenuGroup.class)))
                .then(invocation -> invocation.getArgument(0));

        MenuGroup created = service.create(menuGroup);

        assertThat(created.getName()).isEqualTo(menuGroup.getName());
        assertThat(created.getId()).isNotNull();
    }

    @Test
    @DisplayName("모든 메뉴 그룹을 조회한다")
    void findAll() {
        MenuGroup menuGroup = new MenuGroup();
        when(repository.findAll()).thenReturn(List.of(menuGroup));
        List<MenuGroup> found = service.findAll();

        assertThat(found).isNotEmpty();
    }
}
