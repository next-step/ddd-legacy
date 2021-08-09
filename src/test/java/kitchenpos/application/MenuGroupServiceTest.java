package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("메뉴 그룹 생성")
    void createMenuGroup() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("추천메뉴");

        given(menuGroupRepository.save(any())).willReturn(menuGroup);

        final MenuGroup create = menuGroupService.create(menuGroup);

        assertThat(menuGroup.getName()).isEqualTo(create.getName());
    }

    @ParameterizedTest
    @DisplayName("메뉴 그룹명은 한글자 이상")
    @ValueSource(strings = {"죽", "치킨", "탕수육"})
    public void nameMinimumOne(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        given(menuGroupRepository.save(any())).willReturn(menuGroup);

        MenuGroup createMenuGroup = menuGroupService.create(menuGroup);

        assertThat(createMenuGroup.getName()).isEqualTo(name);
    }

    @DisplayName("메뉴 그룹명 비어있으능 예외")
    @Test
    public void negative() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuGroupService.create(new MenuGroup()));
    }

    @DisplayName("메뉴 그룹명 중복 가능")
    @ParameterizedTest
    @ValueSource(strings = {"죽", "치킨", "탕수육"})
    public void duplicationMenuGroupNameCreate(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        MenuGroup menuGroup2 = new MenuGroup();
        menuGroup2.setName(name);

        given(menuGroupRepository.save(any())).willReturn(menuGroup).willReturn(menuGroup2);

        MenuGroup create = menuGroupService.create(menuGroup);
        MenuGroup create2 = menuGroupService.create(menuGroup2);

        assertThat(create.getName()).isEqualTo(create2.getName());
    }
}