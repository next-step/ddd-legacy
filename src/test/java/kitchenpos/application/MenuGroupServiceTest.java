package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    MenuGroupRepository menuGroupRepository;

    @InjectMocks
    MenuGroupService menuGroupService;

    @ParameterizedTest(name = "메뉴 그룹의 이름은 빈 값을 허용하지 않는다. source = {0}")
    @NullAndEmptySource
    void create_IllegalName(String source) {
        MenuGroup request = new MenuGroup(source);

        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹 생성")
    @Test
    void create() {
        // given
        MenuGroup request = new MenuGroup("두마리치킨");
        when(menuGroupRepository.save(any())).then(i -> i.getArgument(0, MenuGroup.class));

        // when
        MenuGroup saved = menuGroupService.create(request);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("두마리치킨");
    }
}
