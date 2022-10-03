package kitchenpos.application;

import kitchenpos.domain.*;
import factory.MenuGroupFactory;
import kitchenpos.domain.InMemoryMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @BeforeEach
    public void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴그룹을 만들 수 있다.")
    @Test
    public void create(){
        final MenuGroup request = MenuGroupFactory.getDefaultMenuGroup();

        MenuGroup menuGroup = menuGroupService.create(request);

        assertThat(menuGroup.getId()).isNotNull();
        assertThat(menuGroup.getName()).isEqualTo("치킨");
    }

    @NullAndEmptySource
    @ParameterizedTest(name = "메뉴그룹을 생성 시, 메뉴 이름은 필수로 입력되어야 한다.")
    public void create_input_null_and_empty(String name){
        final MenuGroup request = MenuGroupFactory.of(name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(request));
    }

    @DisplayName("메뉴그룹을 조회할 수 있다.")
    @Test
    public void findAll(){
        final MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        menuGroupRepository.save(menuGroup);

        List<MenuGroup> menuGroups = menuGroupService.findAll();

        assertThat(menuGroups).hasSize(1);
    }

}
